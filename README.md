# Ручное построение низходящего парсера арифметических выражений

**Условие:**\
*Арифметические выражения с операциями сложения, вычитания,
умножения, скобками, унарным минусом и унарными функциями. Приоритет операций стандартный. Скобки используются для изменения приоритета и передачи аргументов в функции.
В качестве операндов выступают целые числа. Используйте один терминал для всех чисел. Любая последовательность букв задает имя функции. Используйте один терминал для всех функций.*

Пример: (1+2)*sin(-3*(7-4)+2)

## Построение КС-грамматики

Зададим наивную кс-грамматику по заданному условию
Получим следующие правила:

1. E -> E + T
2. E -> E - T
3. E -> T
4. T -> T * F
5. T -> F
6. F -> -F
7. F -> n
8. F -> (E)
9. F -> f(E)

| Нетерминал | Описание          |
|------------|-------------------|
| E          | Сумма (выражение) |
| T          | Слагаемое         |
| F          | Множитель         |

Заметим, что в строках 1, 2, 4 наблюдается левая рекурсия, а также в строках 1, 2 - правое ветвление
Уберём сначала левую рекурсию, и, при необходимости, правое ветвление

После того, как убрали левую рекурсию, получили следующую грамматику, тут не наблюдается правого ветвления,
значит можно переходить к следующему шагу

E -> TR\
R -> +TR\
R -> -TR\
R ->\
T -> FY\
Y -> *FY\
Y ->\
F -> -F\
F -> n\
F -> (E)\
F -> f(E)

| Нетерминал | Описание                      |
|------------|-------------------------------|
| E          | Начало суммы (выражения)      |
| R          | Продолжение суммы (выражения) |
| T          | Начало слагаемого             |
| Y          | Продолжение слагаемого        |
| F          | Множитель                     |


| Токен | Название |
|-------|----------|
| +     | Plus     |
| -     | Minus    |
| *     | Multiply |
| n     | Number   |
| f     | Name     |
| (     | Open     |
| )     | Close    |

## Построение FIRST и FOLLOW

Воспользуемся теоремой о LL(1) грамматиках,
построим FIRST и FOLLOW и проверим, что
данная грамматика удовлетворяет теореме

```
// first
R = [+, -, ε]
T = [-, n, (, f]
E = [-, n, (, f]
F = [-, n, (, f]
Y = [*, ε]

// follow
E = [$, )]
T = [+, -, $, )]
R = [$, )]
F = [*, +, -, $, )]
Y = [+, -, $, )]
```

Проверим по теореме (с помощью программы),
данная грамматика -- LL(1) грамматика

Пример дерева разбора для примера:\
(1+2)*sin(-3*(7-4)+2)\
https://github.com/LargonG/mt-lab-2/blob/main/sample.svg