# Projekt-Darwin
## Opis projektu 
Projekt ten ma na celu symulowanie ewolucji zwierząt w ustalonym środowisku:
- **kula ziemska** -  lewa i prawa krawędź mapy zapętlają się (jeżeli zwierzak wyjdzie za lewą krawędź, to pojawi się po prawej stronie - a jeżeli za prawą, to po lewej); górna i dolna krawędź mapy to bieguny - nie można tam wejść (jeżeli zwierzak próbuje wyjść poza te krawędzie mapy, to pozostaje na polu na którym był, a jego kierunek zmienia się na odwrotny);
- **bieguny** – bieguny zdefiniowane są na dolnej i górnej krawędzi mapy. Im bliżej bieguna znajduje się zwierzę, tym większą energię traci podczas pojedynczego ruchu (na biegunach jest zimno).

Zwierzęta podczas rozmnażania podlegają jednej z ustalonej mutacji genotypu:
- **pełna losowość** - mutacja zmienia gen na dowolny inny gen;
- **podmianka** - mutacja może też skutkować tym, że dwa geny zamienią się miejscami.

Symulacja każdego dnia składa się z poniższej sekwencji kroków:
1. Usunięcie martwych zwierzaków z mapy.
2. Skręt i przemieszczenie każdego zwierzaka.
3. Konsumpcja roślin, na których pola weszły zwierzaki.
4. Rozmnażanie się najedzonych zwierzaków znajdujących się na tym samym polu.
5. Wzrastanie nowych roślin na wybranych polach mapy.

## Parametry przyjmowane przez program
- wysokość mapy
- szerokość mapy
- początkowa liczba roślin
- energia jednej rośliny
- liczba roślin rosnących każdego dnia
- początkowa liczba zwierząt
- początkowa ilość energii zwierzęcia
- ilość energii traconej każdego dnia
- energia wymagana dla zwięrzęcia do reprodukcji
- energia dawana dziecku przez jednego rodzica
- minimalna liczba mutacji
- maksymalna liczba mutacji
- długość genomu
- typ mapy
- wariant mutacji
- czy dane mają być zapisywane do pliku

## Klasy
Program podzielony jest na wiele klas, z czego najważniejsze to:
- **Animal** - klasa reprezentująca zwierzę, zawierająca logikę poruszania się, jedzenia, rozmnażania, tracenia energii oraz przechowywania genomu
- **Map** - klasa ta reprezentuje mapę świata, na której odbywa się symulacja. Zawiera informacje o zwierzętach i roślinach znajdujących się na mapie oraz zarządza ich rozmieszczeniem i interakcjami.
- **SimulationParameters** - klasa przechowująca parametry wprowadzone przez użytkownika
- **SimulationPresenter** - klasa SimulationPresenter zarządza interfejsem użytkownika oraz cyklem życia symulacji w aplikacji JavaFX. Rozszerza klasę Application i zapewnia metody do inicjalizacji oraz kontrolowania symulacji.
