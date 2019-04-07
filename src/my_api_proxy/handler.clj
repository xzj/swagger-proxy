(ns my-api-proxy.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
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
    #_{:swagger
     {:ui "/"
      :spec "/swagger.json"
      :data {:info {:title "My-api-proxy"
                    :description "Compojure Api example"}
             :tags [{:name "api", :description "some apis"}]}}}

    #_(context "/api" []
             :tags ["api"]

             (GET "/plus" []
                  :return {:result Long}
                  :query-params [x :- Long, y :- Long]
                  :summary "adds two numbers together"
                  (ok {:result (+ x y)}))

             (POST "/echo" []
                   :return Pizza
                   :body [pizza Pizza]
                   :summary "echoes a Pizza"
                   (ok pizza))
             )

    (GET "/doc.json/:id" [id]
         (proxy/doc-proxy-for id)
         )

    (proxy/swagger-ui-routes)
    (proxy/forward-api-route)

    ))
