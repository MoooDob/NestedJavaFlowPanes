# Nested FlowPanes in Java

playing around with nested `FlowPane`s in Java



![nested FlowPanes screenshot](images\screenshot_01.png)



The Panes are randomly created. The maximal height (`setPrefWrapLength`), the number of child panes and the background color of the parent pane can be set. The panes without child panes will be tinted in a random color. All items will be display from top to bottom in column-first order.

The window is scrollable, resizable and pannable. The created panes wont be resized if the window is resized.

