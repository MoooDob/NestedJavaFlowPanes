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

## V 0.3 Nested structure from directory structure

![nested FlowPanes screenshot](./images/screenshot_03a.png)

![nested FlowPanes screenshot](./images/screenshot_03b.png)

This version uses the structure of a directory and its subdirectories to create the nested flowpane structure. The height and the width of the panes representing the files are the lines of code (LOC) and the maximum length of all lines of this file. 

The presentation can be tweaked with the following variables:

```java
float transformFactor = 1.0f;
int seed = 1534;
```

The transformation factor controls if the created boxes are wider, longer or squared. A factor of 0 tries to create squared boxes (if possible), a factor greater 1 creates longer boxes and a factor smaller than 1 creates wider boxes.

The seed simply controls the color of the panes.

You can zoom the content with the mouse wheel.

## V 0.4 Simple vertical layout

![nested FlowPanes screenshot](./images/screenshot_04a.png)

Now it is possible to define if the files should be shown, if the borders should be shown, if a padding around the `FlowPane`s should be used or the size of the gap: 

```java
boolean showFiles = false;
boolean showBorder = false;
boolean usePadding = false;
int gap = 3;
```
This version aligns all node strait top down. 

