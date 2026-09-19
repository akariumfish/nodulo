# nodulo

Nodulo is a 2D action / platformer sandbox with node based scripting.

At its core nodulo is just a basic 2D action game. Top down camera, tiled level, basic lighting. Use wasd to move and the mouse to shoot mobs. Thats it. The difference is that in nodulo you can modify however you want every single system that compose the game. Want to see what append when you shoot 600 bullet each sec ? Or make mobs fight each other ? Change the movement speed, modify the level design, you can even ticker with the game main process to play with the fundamental rules of the world. You will maybe create an awesome effect nobody thought about before ! Or, more realistically, you will got surprising, but nonetheless broken, results. 

The intent of this project is to give back to the player control over his game. In that optic nodulo offert the means to modify the games content and the way the game works using visual node-based scripting. Nodulo will create and manage the systems needed by the game and it make them accessible to the player in a simpler way through the scipting editor. The editor is inside the game and is fully functional while the game is running. Modifications have an immediate effect on the game so testing things is quick and easy. Nodulo is intended to be used by anybody and is trying to be as accessible as possible but in its current state it is still far from easy to use. 

Please be aware that this project is only a prototype and is currently barely fonctional. It is still in early stage of its development and is released only as a proof of concept.


Dependencies :

Nodulo is build in java using the libgdx environment and is using the following libgdx libraries as it or as exemple :

gdx-box2d
box2dlight
gdx-freetype
kryonet
noise4j
steamworks4j
gdx-vfx
shapeDrawer
