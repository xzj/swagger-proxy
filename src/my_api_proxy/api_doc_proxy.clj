(ns my-api-proxy.api-doc-proxy
    (:require [clj-http.client :as client]
              [cheshire.core :as chc]
              [compojure.api.sweet :as cas]
              [compojure.core :as cc]
              [ring.swagger.swagger-ui :as su]
              )
    )

(defn conf []
  {
   :swagger-conf {
                  :server-name "localhost"
                  :server-port 5000
                  :uri "/swagger.json"
                  :scheme :http
                  ; :protocol "HTTP/1.1"
                  }
   :api-server-conf {
                     :server-name "localhost"
                     :server-port 5000
                     :scheme :http
                     }
   :api-doc-filter-rules {
                          "abc" ["/api/pl" #_"/api/e"]
                          "def" [#_"/api/pl" "/api/e"]
                          "mobile" ["/mobile/"]
                          }
   }
  )

(defn all-filter-rules []
  (:api-doc-filter-rules (conf))
  )

(defn swagger-conf []
  (:swagger-conf (conf))
  )

(defn api-server-conf []
  (:api-server-conf (conf))
  )

(defn swagger-ui-routes-for [filter-rules]
  (let [ui-specs (map
                   (fn [[id _]]
                       {:path (str "/my-api-doc/" id)
                        :swagger-docs (str "/doc.json/" id)
                        })
                   filter-rules)
        ui-routes (map su/swagger-ui ui-specs)
        ]
    (apply cas/undocumented ui-routes)
    )
  )

(defn swagger-ui-routes []
  (swagger-ui-routes-for (all-filter-rules))
  )

(defn should-retain-path? [filter-rules [path end-point-spec :as path-spec]]
  (some #(clojure.string/starts-with? path %) filter-rules)
  )

(defn filter-apis [filter-rules swagger-json-str]
  (let [swagger-spec (chc/parse-string swagger-json-str)
        paths-spec (get swagger-spec "paths")
        paths-spec (filter (partial should-retain-path? filter-rules) paths-spec)
        paths-spec (into {} paths-spec)
        swagger-spec (assoc swagger-spec "paths" paths-spec)
        ]
    (chc/generate-string swagger-spec)
    )
  )

(defn get-filter-rules-for [id]
  (let [filter-rules (all-filter-rules)]
    (get filter-rules id)
    )
  )

(defn access-swagger []
  (let [swagger-req (swagger-conf)
        swagger-req (assoc swagger-req :request-method :get)
        ]
    (client/request swagger-req)
    )
  )

(defn doc-proxy-for [id]
  (let [spec-url "http://localhost:5000/swagger.json"
        ; remove-path "/api/plus10"
        ; filter-rules ["/api/pl" "/api/e"]
        filter-rules (get-filter-rules-for id)
        ; resp (client/get spec-url)
        resp (access-swagger)
        {:keys [body headers]} resp
        body (filter-apis filter-rules body)

        ; body (-> body chc/parse-string)
        ; paths (get body "paths")
        ; paths (dissoc paths remove-path)
        ; body (assoc body "paths" paths)
        headers (dissoc headers "content-length")
        ; resp (assoc resp :headers headers :body (chc/generate-string body))
        resp (assoc resp :headers headers :body body)
        ]
    (println " ==== resp: " resp)
    (println)
    resp
    )
  #_"{\"swagger\":\"2.0\",\"info\":{\"title\":\"My-api\",\"version\":\"0.0.1\",\"description\":\"Compojure Api example\"},\"produces\":[\"application/json\",\"application/x-yaml\",\"application/edn\",\"application/transit+json\",\"application/transit+msgpack\"],\"consumes\":[\"application/json\",\"application/x-yaml\",\"application/edn\",\"application/transit+json\",\"application/transit+msgpack\"],\"basePath\":\"/\",\"paths\":{\"/api/plus\":{\"get\":{\"tags\":[\"api\"],\"responses\":{\"200\":{\"schema\":{\"$ref\":\"#/definitions/Response17224\"},\"description\":\"\"}},\"parameters\":[{\"in\":\"query\",\"name\":\"x\",\"description\":\"\",\"required\":true,\"type\":\"integer\",\"format\":\"int64\"},{\"in\":\"query\",\"name\":\"y\",\"description\":\"\",\"required\":true,\"type\":\"integer\",\"format\":\"int64\"}],\"summary\":\"adds two numbers together\"}},\"/api/echo\":{\"post\":{\"tags\":[\"api\"],\"responses\":{\"200\":{\"schema\":{\"$ref\":\"#/definitions/Pizza\"},\"description\":\"\"}},\"parameters\":[{\"in\":\"body\",\"name\":\"Pizza\",\"description\":\"\",\"required\":true,\"schema\":{\"$ref\":\"#/definitions/Pizza\"}}],\"summary\":\"echoes a Pizza\"}}},\"tags\":[{\"name\":\"api\",\"description\":\"some apis\"}],\"definitions\":{\"Pizza\":{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\"},\"description\":{\"type\":\"string\"},\"size\":{\"type\":\"string\",\"enum\":[\"L\",\"M\",\"S\"]},\"origin\":{\"$ref\":\"#/definitions/PizzaOrigin\"}},\"additionalProperties\":false,\"required\":[\"name\",\"size\",\"origin\"]},\"PizzaOrigin\":{\"type\":\"object\",\"properties\":{\"country\":{\"type\":\"string\",\"enum\":[\"PO\",\"FI\"]},\"city\":{\"type\":\"string\"}},\"additionalProperties\":false,\"required\":[\"country\",\"city\"]},\"Response17224\":{\"type\":\"object\",\"properties\":{\"result\":{\"type\":\"integer\",\"format\":\"int64\"}},\"additionalProperties\":false,\"required\":[\"result\"]}}}"
  )

(defn forward-req [{:keys [server-port server-name scheme] :or {server-port 3000 server-name "localhost" scheme :http}} req]
  (let [{:keys [headers body #_server-name #_scheme uri request-method protocol query-string form-params] :as req} req
        headers (dissoc headers "content-length")
        api-req {:server-port server-port :server-name server-name :scheme scheme :request-method request-method
                 :uri uri :protocol protocol :query-string query-string :body body :headers headers
                 #_:form-params #_form-params :throw-exceptions false}
        api-req (if (seq form-params) (assoc api-req :form-params form-params) api-req)
        ]
    (println)
    (println " ====== forward req: " req)
    (client/request api-req)
    )
  )

(defn forward-api-route []
  (cas/undocumented
      (cc/rfn req (-> (api-server-conf) (forward-req req)))
      )
  )

#_(defn remote-plus [req]
  (println (str "req: " req))
  ; (client/request (-> (assoc req :server-port 5000 #_:params #_{:x 1 :y 2}) (dissoc :query-params)))
  (forward-req 5000 req)
  )

#_(defn remote-echo [req]
  (println (str "req: " req))
  (let [{:keys [headers body server-name scheme uri request-method protocol query-string] :as req} req
        ; req (update-in req [:headers] #(dissoc % "content-length"))
        headers (dissoc headers "content-length")
        ]
    ; (println)
    ; (client/request {:url "http://localhost:5000/api/echo" :method :post :body body :headers headers #_:content-type #_:json})
    ; (client/request {:server-port 5000 :server-name server-name :request-method request-method :scheme scheme :uri uri :protocol protocol :query-string query-string :body body :headers headers #_:content-type #_:json})
    (forward-req 5000 req)
    )
  )
