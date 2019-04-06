(ns my-api-proxy.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.swagger.swagger-ui :as su]
            [ring.swagger.validator :as sv]
            [schema.core :as s]
            [my-api-proxy.api-doc-proxy :as proxy]
            ))

sv/validate

(s/defschema Pizza
  {:name s/Str
   (s/optional-key :description) s/Str
   :size (s/enum :L :M :S)
   :origin {:country (s/enum :FI :PO)
            :city s/Str}})

(def app
  (api
    {:swagger
     {:ui "/"
      :spec "/swagger.json"
      :data {:info {:title "My-api-proxy"
                    :description "Compojure Api example"}
             :tags [{:name "api", :description "some apis"}]}}}

    (context "/api" []
             :tags ["api"]

             #_(GET "/plus" []
                  :return {:result Long}
                  :query-params [x :- Long, y :- Long]
                  :summary "adds two numbers together"
                  (ok {:result (+ x y)}))

             #_(POST "/echo" []
                   :return Pizza
                   :body [pizza Pizza]
                   :summary "echoes a Pizza"
                   (ok pizza))

             (GET "/plus" req
                  ; :return {:result Long}
                  ; :query-params [x :- Long, y :- Long]
                  ; :summary "adds two numbers together"
                  (proxy/remote-plus req))

             (POST "/echo" req
                   ; :return Pizza
                   ; :body [pizza Pizza]
                   ; :summary "echoes a Pizza"
                   (proxy/remote-echo req))
             )

    (GET "/show-req" request
         (ok (str request))
         )

    (GET "/doc.json" []
         ; :return String
         (proxy/doc-proxy)
         #_(ok (proxy/doc-proxy)))

    (su/swagger-ui {:path "/my-api-doc"
                    :swagger-docs "/doc.json"
                    })
    ))
