(ns my-api-proxy.api-doc-proxy
    (:require [clj-http.client :as client])
    )

(defn doc-proxy []
  (let [spec-url "http://localhost:5000/swagger.json"
        resp (client/get spec-url)
        ]
    resp
    )
  #_"{\"swagger\":\"2.0\",\"info\":{\"title\":\"My-api\",\"version\":\"0.0.1\",\"description\":\"Compojure Api example\"},\"produces\":[\"application/json\",\"application/x-yaml\",\"application/edn\",\"application/transit+json\",\"application/transit+msgpack\"],\"consumes\":[\"application/json\",\"application/x-yaml\",\"application/edn\",\"application/transit+json\",\"application/transit+msgpack\"],\"basePath\":\"/\",\"paths\":{\"/api/plus\":{\"get\":{\"tags\":[\"api\"],\"responses\":{\"200\":{\"schema\":{\"$ref\":\"#/definitions/Response17224\"},\"description\":\"\"}},\"parameters\":[{\"in\":\"query\",\"name\":\"x\",\"description\":\"\",\"required\":true,\"type\":\"integer\",\"format\":\"int64\"},{\"in\":\"query\",\"name\":\"y\",\"description\":\"\",\"required\":true,\"type\":\"integer\",\"format\":\"int64\"}],\"summary\":\"adds two numbers together\"}},\"/api/echo\":{\"post\":{\"tags\":[\"api\"],\"responses\":{\"200\":{\"schema\":{\"$ref\":\"#/definitions/Pizza\"},\"description\":\"\"}},\"parameters\":[{\"in\":\"body\",\"name\":\"Pizza\",\"description\":\"\",\"required\":true,\"schema\":{\"$ref\":\"#/definitions/Pizza\"}}],\"summary\":\"echoes a Pizza\"}}},\"tags\":[{\"name\":\"api\",\"description\":\"some apis\"}],\"definitions\":{\"Pizza\":{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\"},\"description\":{\"type\":\"string\"},\"size\":{\"type\":\"string\",\"enum\":[\"L\",\"M\",\"S\"]},\"origin\":{\"$ref\":\"#/definitions/PizzaOrigin\"}},\"additionalProperties\":false,\"required\":[\"name\",\"size\",\"origin\"]},\"PizzaOrigin\":{\"type\":\"object\",\"properties\":{\"country\":{\"type\":\"string\",\"enum\":[\"PO\",\"FI\"]},\"city\":{\"type\":\"string\"}},\"additionalProperties\":false,\"required\":[\"country\",\"city\"]},\"Response17224\":{\"type\":\"object\",\"properties\":{\"result\":{\"type\":\"integer\",\"format\":\"int64\"}},\"additionalProperties\":false,\"required\":[\"result\"]}}}"
  )
