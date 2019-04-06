(ns my-api-proxy.api-doc-proxy
    (:require [clj-http.client :as client]
              [cheshire.core :as cc]
              )
    )

(defn doc-proxy []
  (let [spec-url "http://localhost:5000/swagger.json"
        remove-path "/api/plus"
        resp (client/get spec-url)
        {:keys [body headers]} resp
        body (-> body cc/parse-string)
        paths (get body "paths")
        paths (dissoc paths remove-path)
        body (assoc body "paths" paths)
        headers (dissoc headers "content-length")
        resp (assoc resp :headers headers :body (cc/generate-string body))
        ]
    (println " ==== resp: " resp)
    (println)
    resp
    )
  #_"{\"swagger\":\"2.0\",\"info\":{\"title\":\"My-api\",\"version\":\"0.0.1\",\"description\":\"Compojure Api example\"},\"produces\":[\"application/json\",\"application/x-yaml\",\"application/edn\",\"application/transit+json\",\"application/transit+msgpack\"],\"consumes\":[\"application/json\",\"application/x-yaml\",\"application/edn\",\"application/transit+json\",\"application/transit+msgpack\"],\"basePath\":\"/\",\"paths\":{\"/api/plus\":{\"get\":{\"tags\":[\"api\"],\"responses\":{\"200\":{\"schema\":{\"$ref\":\"#/definitions/Response17224\"},\"description\":\"\"}},\"parameters\":[{\"in\":\"query\",\"name\":\"x\",\"description\":\"\",\"required\":true,\"type\":\"integer\",\"format\":\"int64\"},{\"in\":\"query\",\"name\":\"y\",\"description\":\"\",\"required\":true,\"type\":\"integer\",\"format\":\"int64\"}],\"summary\":\"adds two numbers together\"}},\"/api/echo\":{\"post\":{\"tags\":[\"api\"],\"responses\":{\"200\":{\"schema\":{\"$ref\":\"#/definitions/Pizza\"},\"description\":\"\"}},\"parameters\":[{\"in\":\"body\",\"name\":\"Pizza\",\"description\":\"\",\"required\":true,\"schema\":{\"$ref\":\"#/definitions/Pizza\"}}],\"summary\":\"echoes a Pizza\"}}},\"tags\":[{\"name\":\"api\",\"description\":\"some apis\"}],\"definitions\":{\"Pizza\":{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\"},\"description\":{\"type\":\"string\"},\"size\":{\"type\":\"string\",\"enum\":[\"L\",\"M\",\"S\"]},\"origin\":{\"$ref\":\"#/definitions/PizzaOrigin\"}},\"additionalProperties\":false,\"required\":[\"name\",\"size\",\"origin\"]},\"PizzaOrigin\":{\"type\":\"object\",\"properties\":{\"country\":{\"type\":\"string\",\"enum\":[\"PO\",\"FI\"]},\"city\":{\"type\":\"string\"}},\"additionalProperties\":false,\"required\":[\"country\",\"city\"]},\"Response17224\":{\"type\":\"object\",\"properties\":{\"result\":{\"type\":\"integer\",\"format\":\"int64\"}},\"additionalProperties\":false,\"required\":[\"result\"]}}}"
  )

(defn remote-req [port req]
  (let [{:keys [headers body server-name scheme uri request-method protocol query-string] :as req} req
        headers (dissoc headers "content-length")
        ]
    (client/request {:server-port port :server-name server-name :request-method request-method :scheme scheme :uri uri :protocol protocol :query-string query-string :body body :headers headers})
    )
  )

(defn remote-plus [req]
  (println (str "req: " req))
  ; (client/request (-> (assoc req :server-port 5000 #_:params #_{:x 1 :y 2}) (dissoc :query-params)))
  (remote-req 5000 req)
  )

(defn remote-echo [req]
  (println (str "req: " req))
  (let [{:keys [headers body server-name scheme uri request-method protocol query-string] :as req} req
        ; req (update-in req [:headers] #(dissoc % "content-length"))
        headers (dissoc headers "content-length")
        ]
    ; (println)
    ; (client/request {:url "http://localhost:5000/api/echo" :method :post :body body :headers headers #_:content-type #_:json})
    ; (client/request {:server-port 5000 :server-name server-name :request-method request-method :scheme scheme :uri uri :protocol protocol :query-string query-string :body body :headers headers #_:content-type #_:json})
    (remote-req 5000 req)
    )
  )
