# Nested FlowPanes in Java

playing around with nested `FlowPane`s in Java

## V 0.1: two statically nested FlowPanes

[91b8cc214d73975742ca01a9ada3eb9ee1fddd99](https://github.com/MoooDob/NestedJavaFlowPanes/commit/91b8cc214d73975742ca01a9ada3eb9ee1fddd99)

![nested FlowPanes screenshot](./images/screenshot_01.png)



The Panes are randomly created. The maximal height (`setPrefWrapLength`), the number of child panes and the background color of the parent pane can be set. The panes without child panes will be tinted in a random color. All items will be display from top to bottom in column-first order.

The window is scrollable, resizable and pannable. The created panes wont be resized if the window is resized.

## V 0.2: randomly created structures of nested FlowPanes

![nested FlowPanes screenshot](./images/screenshot_02.png)

![nested FlowPanes screenshot](./images/screenshot_02b.png)

This time the structure is created randomly. You can control the structure by changing the following parameters:

```java
int maxNestingDepth = 10;
int maxNumberOfPanes = 30;
int maxNumberOfChilds = 3;
int seed = 1534;
```

`maxNumberOfPanes` declares the number of simple panes per nesting level (without child panes). In the last version, only the *leaf* panes will be colorized, all *branching* panes get only a black border. You can change this behavior by uncommenting the line `flowPane.setStyle`.

In each flow pane the items are sorted by number. Their height and size is randomly calculated. The maximal height of a pane is the maximum out of the height of the squared area of all child panes (child flow pane too) and the height of the highest child pane (child flow panes too), 