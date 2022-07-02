# gameoflife
(WIP) A Java implementation of Conway's Game of Life
https://en.wikipedia.org/wiki/Conway's_Game_of_Life

Issues:
- Buttons won't appear unless you click outside the window, then back inside,

- You can click each cell to kill/birth, but the calibration isn't perfect so you'll need to click in the very centre for accuracy,

- Resizing the app during a generation may cause it to crash (so it's been disabled),

- The top of the matrix is cut off, with at least one row being obscured by the title bar,

- Gen progression causes the last line of the matrix to flicker, as all repaints currently require the app to redraw.
This is likely tied to CPU speed, I need to implement the smart redraw method from elsewhere in the app.  Also, if grid is toggled off each living
cell will flicker due to the same repaint redraw issue (I presume).
