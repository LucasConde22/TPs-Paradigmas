# TP2: Sistemas-L

## Integrantes:

Colombo Farre, Iván Joel

Conde Cardó, Lucas Ariel

## Introducción:

La excéntrica coleccionista de arte Czarina Halenur quiere redecorar su lujosa mansión, y para ello encargó una serie de cuadros a los famosos artistas post-modernos Lazzi Lacsulu e Italics Carnaroli.

Luego de una ardua reunión de *brainstorming*, la temática elegida resultó ser imágenes generadas por computadora. Los artistas subcontratarán al mejor equipo de arte digital de los últimos 50 años: el curso de Paradigmas de Programación “Adagio Yeses - Aromatising Gaga” de FIUBA. El jurado estará compuesto, entre otros, por los bohemios Gitano Scoriac, Defi Tannic, Vans Acta Amancheriez y Rejoneadora Ballon Amtrac.

## Consigna:

Implementar un programa en **Clojure** que permita generar imágenes fractales, mediante un algoritmo basado en **sistemas-L**, una simulación de **gráficos tortuga** y el formato de imágenes estándar **SVG**.

## Ejecución:

En la carpeta raíz del proyecto (tp2), se debe correr el programa, desde la consola, utilizando los siguientes parámetros:

- Ruta del archivo que contiene la descripción del sistema-L.
- Cantidad de iteraciones a procesar.
- Ruta del archivo SVG a escribir.

Ejemplo:

`lein run arbol.sl 3 arbol.svg`

O, si se prefiere REPL:

`(tp2.core/-main "arbol.sl" "3" "arbol.svg")`

Aclaración:

Desde IntelliJ pueden utilizarse ambos métodos. A la hora de correrlo seleccionar el preferido y, en caso de ejecutarlo como aplicación, especificar los parámetros desde la configuración.



## Extras:

- `a`: Cambia color a azul.
- `b`: Cambia color a rojo.
- `h`: Cambia color a verde.
- `y`: Cambia color a amarillo.
- `1`: Cambia grosor a 1 (por defecto).
- `2`: Cambia grosor a 2.
- `4`: Cambia grosor a 4.
- `c`: Hace que las líneas tengan curvas internas (`r` para desactivar).
- `l`: Dibuja un círculo.
- `e`: Dibuja un 👽.
