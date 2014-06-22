ProScene
========

# Description

[ProScene](http://nakednous.github.io/projects/proscene/) (pronounced similar as the Czech word **"prosím"** which means **"please"**) is a free-software java library which provides classes to ease the creation of interactive 3D scenes in [Processing](http://processing.org).

**ProScene** extensively uses **interactive frames**, i.e., coordinate systems that can be controlled with any [HID](http://en.wikipedia.org/wiki/Human_interface_device), allowing to easily setup an interactive 2D or 3D scene.

**ProScene** provides seemless integration with **Processing**: its API has been designed to fit that of **Processing** and its implementation has been optimized to work along side with it. It suppports all major **Processing** flavours: Desktop, JS, and Android.

**ProScene** support is led by the active and great Processing community at its [forum](http://forum.processing.org/search/proscene) where you can reach us.

# Key features

* *Tested* under Linux, Mac OSX and Windows, and properly works with the JAVA2D, P2D, and P3D Processing renderers. No special dependencies or requirements needed (apart of course from [Processing-2.x](http://processing.org/)).
* Full suppport for Desktop **Processing** mode.
* Basic suppport for Android **Processing** mode. Development is taken place at this [fork](https://github.com/remixlab/proscene.droid). We hope to integrate it back upstream once TouchEvents are directly supported in P5.
* API design that provides seemless integration with **Processing** (e.g., providing flexible animation and drawing mechanisms), and allows extensibility of its key features.
* Default interactivity to your *Processing* scenes through the mouse and keyboard that simply does what you expect.
* Generic suppport for [Human Interface Devices](http://en.wikipedia.org/wiki/Human_interface_device).
* Arcball, first-person and third-person camera modes.
* Hierarchical coordinate systems (frames), with functions to convert between them.
* Keyframes.
* Object picking.
* Full keyboard and camera customization.
* Animation framework.
* Back-face and view-frustum culling.
* Screen drawing (i.e., drawing of 2D primitives on top of a 3D scene).
* Off-screen rendering mode support.
* Handy set of complete documented examples that illustrates the use of the package.
* Released under the terms of the [GPL-v3](http://www.gnu.org/copyleft/gpl.html).
* A complete [reference documentation](http://otrolado.info/prosceneApi/).
* Active support and continuous discussions led by the [Processing community](http://forum.processing.org/two/search?Search=proscene).

# Origin of the name

*ProScene* not only means a *"pro-scene"*, but it is a two-phoneme word pronounced similar as the Czech word *"prosím"* (which means *"please"*), obtained by removing the middle phoneme (*"ce"*) of the word *pro-ce-ssing*. Thus, the name *"ProScene"* suggests the main goal of the package, which is to help you _shorten_ the creation of interactive 3D scenes in *Processing*.

# Usage

All library features requires a `Scene` object (which is the main package class) to be instantiated (usually within your sketch setup method). There are three ways to do that:

1. **Direct instantiation**. In this case you should instantiate your own Scene object at the `PApplet.setup()` function.
2. **Inheritance**. In this case, once you declare a `Scene` derived class, you should implement `proscenium()` which defines the objects in your scene. Just make sure to define the `PApplet.draw()` method, even if it's empty.
3. **External draw handler registration**. You can even declare an external drawing method and then register it at the Scene with `addDrawHandler(Object, String)`. That method should return `void` and have one single `Scene` parameter. This strategy may be useful when you have the same drawing code shared among multiple viewers.

See the examples **BasicUse**, **AlternativeUse**, and **StandardCamera** for an illustration of these techniques. To get start using the library and learn its main features, have a look at the complete set of well documented examples that come along with it. Other uses are also covered in the example set and include (but are not limited to): drawing mechanisms, animation framework, and camera and keyboard customization. Advanced users may take full advantage of the fully documented [API reference](http://www.disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/reference/index.html) (which is also included in the package file).

# Acknowledgements

Thanks to [Eduardo Moriana](http://edumo.net/) and [Miguel Parra](http://maparrar.github.io/) for their contributions with the [TUIO](http://www.tuio.org/)-based touch and kinect interfaces, respectively.
Thanks to experimental computational designer [Amnon Owed](https://twitter.com/AmnonOwed/media) for his collaboration with polishing the KeyFrameInterpolator sub-system.
Thanks to [Jacques Maire](http://www.xelyx.fr) for providing most of the examples found at the *contrib* section. Thansk to [Andres Colubri](http://codeanticode.wordpress.com/) for his continuous support and thorough insights.
Thanks to [Victor Forero](https://sites.google.com/site/proscenedroi/home) who is developing the [proscene Android port](https://github.com/remixlab/proscene.droid).
Finally, thanks to all **ProScene** users whose sketchs and library hacks always amaze us and inspire us.

# Author, core developer and maintainer

[Jean Pierre Charalambos](http://disi.unal.edu.co/profesores/pierre/), [National University of Colombia](http://www.unal.edu.co)
