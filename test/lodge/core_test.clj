(ns lodge.core-test
  (:require [clojure.test :refer :all]
            [lodge.core :refer :all]))

(def testmap-1
  {:a 1 :b 2 :c 3}  )

(def testmap-2
  {:a 1 :b {:ba 1 :bb 2} :c 3}  )

(def testmap-3
  {:a 1 :b {:ba 1 :bb {:a 1 :b 2}} :c 3}  )

(def test-patrol-valid
  {:a integer? :b {:ba string? :bc vector?}}  )

(def test-patrol-hash-t
  {:a 20 :b {:ba "ba" :bc [1 2 3]}})

(def test-patrol-hash-f
  {:a 20 :b {:ba "ba" :bc 3}})

(def test-patrol-hash-f-g
  {:a 20 :b {:ba "ba" :bc 3} :c 4})

(def test-patrol-hash-t-g
  {:a 20 :b {:ba "ba" :bc [1 2 3]} :c 4})

;;;;;;;;;;;;;;;;;

(deftest alltest
  (testing "Roomlist"
  (is (= (roomlist [] [] {:a 1 :b 2 :c 3})
    [[:a] [:b] [:c]]  ))
  (is (= (roomlist [] [] testmap-2)
    [[:a] [:b :ba] [:b :bb] [:c]]  ))
  (is (= (roomlist testmap-3)
    [[:a] [:b :ba] [:b :bb :a] [:b :bb :b] [:c]]  ))
  (is (= (roomlist {})
    []  ))  )

  (testing "Lodgers"
  (is (= (lodgers testmap-3)
    '(1 1 1 2 3)  ))
  (is (= (lodgers {})
    '()  ))  )

  (testing "Samemodel?"
  (is (= (samemodel? testmap-3 testmap-3 (assoc testmap-3 :db 4))
    false  ))
  (is (= (samemodel? testmap-3 testmap-3 (assoc testmap-3 :c 4))
    true  ))
  (is (= (samemodel? testmap-3 {})
    false  ))  )

  (testing "Sublodge?"
  (is (= (sublodge? testmap-3 {:a 1})
    true  ))
  (is (= (sublodge? testmap-3 {:a 1 :b 1})
    false ))
  (is (= (sublodge? testmap-3 {:a 1 :c 2})
    true  ))
  (is (= (sublodge? testmap-3 {:a 1 :b {:ba 1}})
    true  ))
  (is (= (sublodge? testmap-3 {})
    true  ))  )

  (testing "Patrol"
  (is (= (patrol test-patrol-valid test-patrol-hash-t)
    true   ))
  (is (= (patrol test-patrol-valid test-patrol-hash-f)
    '([:b :bc])  ))
  (is (= (patrol test-patrol-valid test-patrol-hash-t-g)
    false  ))  )

  (testing "Subpatrol"
  (is (= (subpatrol test-patrol-valid test-patrol-hash-t-g)
    true  ))
  (is (= (subpatrol test-patrol-valid test-patrol-hash-f-g)
    '([:b :bc])  ))  )

  (testing "Update-with"
  (is (= (update-with {:a inc :b {:ba dec :bb #(str % "+")}}
           {:a 1 :b {:ba 1 :bb "bb"}}  )
    {:a 2, :b {:ba 0, :bb "bb+"}}  ))
  (is (= (assoc-with {:a 2 :b {:ba 0 :bb "bb+"}}
           {:a 1 :b {:ba 1 :bb "bb"}})
    {:a 2, :b {:ba 0, :bb "bb+"}}  ))  ))

;;;;;;;;;;;;


