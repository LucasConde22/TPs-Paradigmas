(ns tp2.core
  (:require [clojure.java.io :as modulo_io]
            [clojure.string :as str]))

(defn determinar-dimensiones [formas]
  ;; Determina las dimensiones y los márgenes del archivo .svg.
  (let [xs (mapcat (fn [forma]
                     (case (:tipo forma)
                       :linea [(:x1 forma) (:x2 forma)]
                       :circulo [(- (:cx forma) (:r forma)) (+ (:cx forma) (:r forma))]
                       [])) formas)
        ys (mapcat (fn [forma]
                     (case (:tipo forma)
                       :linea [(:y1 forma) (:y2 forma)]
                       :circulo [(- (:cy forma) (:r forma)) (+ (:cy forma) (:r forma))]
                       [])) formas)
        margen 20 ; Cambiar si se desea modificar el margen.
        min-x (apply min xs)
        max-x (apply max xs)
        min-y (apply min ys)
        max-y (apply max ys)
        ancho (+ (- max-x min-x) (* 2 margen))
        alto (+ (- max-y min-y) (* 2 margen))]
    [(- min-x margen) (- min-y margen) ancho alto]))


(defn parsear-lineas [lineas]
  ;; Convierte líneas y círculos a strings con los tags de svg.
  (->> lineas
       (map (fn [linea]
              (case (:tipo linea)
                :linea
                (let [{:keys [x1 y1 x2 y2 color grosor curvada?]} linea]
                  (if curvada?
                    (String/format java.util.Locale/US
                                   "<path d=\"M %.3f %.3f Q %.3f %.3f %.3f %.3f\" stroke-width=\"%d\" stroke=\"%s\" fill=\"none\" />"
                                   (to-array [x1 y1 (/ (+ x1 x2) 2) (- (/ (+ y1 y2) 2) 10) x2 y2 grosor color]))
                    (String/format java.util.Locale/US
                                   "<line x1=\"%.3f\" y1=\"%.3f\" x2=\"%.3f\" y2=\"%.3f\" stroke-width=\"%d\" stroke=\"%s\" />"
                                   (to-array [x1 y1 x2 y2 grosor color]))))
                :circulo
                (let [{:keys [cx cy r color grosor]} linea]
                  (String/format java.util.Locale/US
                                 "<circle cx=\"%.3f\" cy=\"%.3f\" r=\"%.3f\" stroke=\"%s\" stroke-width=\"%d\" fill=\"none\" />"
                                 (to-array [cx cy r color grosor])))
                :texto
                (let [{:keys [x y contenido tamano]} linea]
                  (String/format java.util.Locale/US
                                 "<text x=\"%.3f\" y=\"%.3f\" font-size=\"%d\" font-family=\"Noto Color Emoji, sans-serif\">%s</text>"
                                 (to-array [x y tamano contenido]))))))
       (filter identity)
       (str/join "\n")))

(defn pasar-lineas-a-svg [ruta lineas]
  ;; Armar un string con todas las líneas y escribe el archivo
  ;; .svg con el formato requerido para generar el gráfico.
  (let [[min-x min-y ancho alto] (determinar-dimensiones lineas)
        contenido-lineas (parsear-lineas lineas)
        contenido (String/format java.util.Locale/US
                                 "<svg viewBox=\"%.3f %.3f %.3f %.3f\" xmlns=\"http://www.w3.org/2000/svg\">\n%s\n</svg>"
                                 (to-array [min-x min-y ancho alto contenido-lineas]))]
    (spit ruta contenido))) ; spit escribe la cadena armada en el archivo .svg

(defn avanzar [tortuga]
  ;; Halla las coordenadas de finalización de la línea.
  (let [{:keys [x y dir pluma? color grosor curvada?]} tortuga
        escala 10 ; Modificar este nro. si se quiere cambiar la escala.
        x2 (+ x (* escala (Math/cos dir)))
        y2 (- y (* escala (Math/sin dir)))
        linea (when pluma?
                {:tipo :linea
                 :x1 x :y1 y :x2 x2 :y2 y2
                 :color color
                 :grosor grosor
                 :curvada? curvada?})]
    [(assoc tortuga :x x2 :y y2 :pluma? pluma?) linea]))

(defn girar [tortuga delta]
  ;; Gira la dirección de la tortuga.
  (update tortuga :dir #(+ % delta)))

(defn angulo-a-radianes [angulo]
  ;; Calcula ángulo*(pi/180)
  (* angulo (/ Math/PI 180.0)))

(defn pasar-sisl-a-tortuga [angulo sisl]
  ;; Genera las coordenadas correspondientes a cada línea
  ;; realizando la transformación correspondiente desde el
  ;; sistema-L obtenido anteriormente a 'gráficos tortuga'

  ;; El avance de posición está dado por:
  ;; (x, y) -> (x + con(ángulo), y + sin(ángulo))
  ;;Por lo tanto, el ángulo debe estar en radianes.
  (let [radianes (angulo-a-radianes angulo)
        inicial {:x 0.0 :y 0.0 :dir (/ Math/PI 2.0) :pluma? true :color "black" :grosor 1 :curvada? false}] ; Valores iniciales

    (loop [tortuga inicial
           pila []
           operaciones (seq sisl)
           lineas []]

      (if (empty? operaciones)
        lineas
        (let [operacion (first operaciones)
              [nueva-tortuga nueva-pila linea]
              (case operacion
                (\F \G)
                (let [[t l] (avanzar tortuga)] [t pila l])
                (\f \g)
                (let [[t l] (avanzar (assoc tortuga :pluma? false))]
                  [(assoc t :pluma? true) pila l])
                \+
                [(girar tortuga (- radianes)) pila nil]
                \-
                [(girar tortuga radianes) pila nil]
                \|
                [(girar tortuga Math/PI) pila nil]
                \[
                [tortuga (conj pila tortuga) nil]
                \]
                [(peek pila) (pop pila) nil]
                \a
                [(assoc tortuga :color "blue") pila nil] ; Azul.
                \b
                [(assoc tortuga :color "red") pila nil] ; Rojo.
                \h
                [(assoc tortuga :color "green") pila nil] ; Verde.
                \y
                [(assoc tortuga :color "yellow") pila nil] ; Amarillo.
                \1
                [(assoc tortuga :grosor 1) pila nil]
                \2
                [(assoc tortuga :grosor 2) pila nil]
                \4
                [(assoc tortuga :grosor 4) pila nil] ; De la misma forma se podrían agregar más grosores.
                \c
                [(assoc tortuga :curvada? true) pila nil] ; La línea será curvada.
                \r
                [(assoc tortuga :curvada? false) pila nil] ; La línea será recta.
                \l ; Círculo
                (let [{:keys [x y color grosor]} tortuga radio 5.0] ; Modificar si se desea otro radio.
                  [tortuga pila {:tipo :circulo
                                 :cx x :cy y :r radio
                                 :color color :grosor grosor}])
                \e
                (let [{:keys [x y]} tortuga
                      texto "👽" ; Emoji
                      tam 14] ; Modificar si se desea otro tamaño.
                  [tortuga pila {:tipo :texto
                                 :x x :y y
                                 :contenido texto
                                 :tam tam}])

                [tortuga pila nil])] ; No hace nada si no es ninguna opción.

          (recur nueva-tortuga
                 nueva-pila
                 (rest operaciones)
                 (if linea (conj lineas linea) lineas)))))))

(defn expandir-sisl [axioma reglas iteraciones]
  ;; Expande el sistema L inicial (el axioma), siguiendo las reglas, la cantidad de veces indicada.
  (loop [actual axioma
         n iteraciones]
    (if (zero? n)
      actual
      (let [siguiente (apply str (map #(get reglas % (str %)) actual))]
        (recur siguiente (dec n))))))

(defn leer-archivo-sisl [ruta]
  ;; Lee el archivo que contiene la descripción del sistema-L y realiza los parseos correspondientes.
  (with-open [lector (modulo_io/reader ruta)]
    (let [lineas (line-seq lector)
          angulo (float (read-string (first lineas)))
          axioma (second lineas)
          reglas (->> (drop 2 lineas)
                      (map #(str/split % #"\s+"))
                      (map (fn [[predecesor sucesor]]
                             [(first predecesor) sucesor]))
                      (into {}))]
      {:angulo angulo
       :axioma axioma
       :reglas reglas})))

(defn -main [& args]
  (let [[archivo-sisl iteraciones archivo-svg] args
        iteraciones (Integer/parseInt iteraciones)
        {:keys [angulo axioma reglas]} (leer-archivo-sisl archivo-sisl)
        expansion (expandir-sisl axioma reglas iteraciones)
        lineas (pasar-sisl-a-tortuga angulo expansion)]
    (pasar-lineas-a-svg archivo-svg lineas)))

