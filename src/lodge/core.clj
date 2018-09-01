(ns lodge.core
  (:require 
    [clojure.set :as set]))



(defn roomlist
  ([rawmap] (roomlist [] [] rawmap))
  ([container header rawmap]
    (let [atmap? #(map? (% rawmap))]
      (loop [ks (keys rawmap)  cn container]
        (let [fk (first ks)  hfk (conj header fk)]
          (if (empty? ks) cn
            (recur
              (rest ks)
              (if (atmap? fk)
                  (roomlist cn hfk (fk rawmap))
                  (conj cn hfk))  )))))))
;; => (roomlist [] [] {:a 1 :b 2 :c 3})
;; [[:a] [:b] [:c]]
;; => (roomlist [] [] {:a 1 :b {:ba 1 :bb 2} :c 3})
;; [[:a] [:b :ba] [:b :bb] [:c]]
;; => (roomlist [] [] {:a 1 :b {:ba 1 :bb {:a 1 :b 2}} :c 3})
;; ;; equals with (roomlist themap)
;; [[:a] [:b :ba] [:b :bb :a] [:b :bb :b] [:c]]


(defn lodgers [rawmap]
  (map #(get-in rawmap %) (roomlist rawmap))  )
;; => (lodgers {:a 1 :b {:ba 1 :bb {:a 1 :b 2}} :c 3})
;; (1 1 1 2 3)



(defn samemodel? [& lodges]
  (apply = (map #(-> % roomlist set) lodges))  )
;; (def testmap  {:a 1 :b {:ba 1 :bb {:a 1 :b 2}} :c 3})
;; => (samemodel? testmap testmap (assoc testmap :db 4))
;; false
;; => (samemodel? testmap testmap (assoc testmap :c 4))
;; true



(defn sublodge? [super sub]
  (let [ssmap (map #(-> % roomlist set) [super sub])]
    (clojure.set/superset? (first ssmap) (last ssmap))  ))
;; ringtest.core=> (map #(-> % roomlist set) [testmap {:a 1 :b 1}])
;; (#{[:b :ba] [:c] [:a] [:b :bb :b] [:b :bb :a]} #{[:b] [:a]})
;; => (sublodge? testmap {:a 1})
;; true
;; => (sublodge? testmap {:a 1 :b 1})
;; false
;; => (sublodge? testmap {:a 1 :c 2})
;; true



(defn- _patrol [subfn directory lodge]
  (if-not (subfn lodge directory) false
    (let [dks (roomlist directory)
          checkf #((get-in directory %) (get-in lodge %))
          result (map #(if (checkf %) true %) dks)]
      (if (every? true? result)
          true
          (remove true? result)  ))))
(defn patrol [directory lodge]
  (_patrol samemodel? directory lodge))
(defn subpatrol [directory lodge]
  (_patrol sublodge? directory lodge))
;; => (patrol {:a integer? :b {:ba string? :bc vector?}}
;;            {:a 20 :b {:ba "ba" :bc [1 2 3]}})
;; true
;; => (patrol {:a integer? :b {:ba string? :bc vector?}}
;;            {:a 20 :b {:ba "ba" :bc 3}})
;; ([:b :bc])



(defmacro _-with [appfn dinners lodge]
  `(if-not (sublodge? ~lodge ~dinners) false
     (loop [dones# ~lodge
            rooms# (roomlist ~dinners)]
       (let [moment# (first rooms#)]
         (if (empty? rooms#) dones#
           (recur (~appfn dones# moment# (get-in ~dinners moment#))
                  (rest rooms#)  ))))))

(defmacro update-with [dinners lodge]
  `(_-with update-in ~dinners ~lodge))

(defmacro assoc-with [dinners lodge]
  `(_-with assoc-in ~dinners ~lodge))

;; (update-with {:a inc :b {:ba dec :bb #(str % "+")}}
;;             {:a 1 :b {:ba 1 :bb "bb"}})
;; => {:a 2, :b {:ba 0, :bb "bb+"}}

;; (assoc-with {:a 2 :b {:ba 0 :bb "bb+"}}
;;              {:a 1 :b {:ba 1 :bb "bb"}})
;; => {:a 2, :b {:ba 0, :bb "bb+"}}

