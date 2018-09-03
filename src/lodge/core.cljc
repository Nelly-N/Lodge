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


(defn lodgers [rawmap]
  (map #(get-in rawmap %) (roomlist rawmap))  )


(defn samemodel? [& lodges]
  (apply = (map #(-> % roomlist set) lodges))  )


(defn sublodge? [super sub]
  (let [ssmap (map #(-> % roomlist set) [super sub])]
    (clojure.set/superset? (first ssmap) (last ssmap))  ))


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
