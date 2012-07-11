FlyInMenu
==========

Implementation of the emerging fly-in menu pattern for Android apps. This utilises a high-level modification of the application window to add the fly-in menu (FIM), which is loaded from a menu resource.

This implementation is based on RibbonMenu (Copyright David Scott), but much expanded. It includes support for SearchViews and custom views, improved animations (with two different styles) and is cleaner to use.


Usage
=====

Basic usage
----
Set your activity to override `FlyInFragmentActivity`. You can load the FIM at any time by calling `loadFlyInMenu(..)`, after which you can attach custom views (`getFlyInMenu().setCustomView(..)`) or initalise a SearchView (below).

The fly-in animation can be one of two options set using `setFlyInType(..)`.
* `FLY_IN_OVER_ACTIVITY` brings the fly-in menu over the Activity, obscuring it (left screenshot). 
* `FLY_IN_WITH_ACTIVITY` pushes the Activity's contents to the right (right screenshot).

![Fly over image](https://github.com/Espiandev/FlyInMenu/raw/master/screen_fly_over.png)![Fly with image](https://github.com/Espiandev/FlyInMenu/raw/master/screen_fly_with.png)

SearchView
-----
You can enable a SearchView widget for the top of the FIM. After you have loaded your FIM (see above), call `enableSearchView()` on the FIM instance, which can be obtained using `getFlyInMenu()`. Once `enableSearchView()` has been called, you can retrieve and act on the SearchView with `FlyInMenu.getSearchView()`. Bear in mind, this won't work below Android 3.0, and you should place any calls dealing with the SearchView in conditional clauses.

Known Issues/Upcoming features
======

* The fly-in style isn't currently retained over configuration changes, fix coming soon
* Nexus 7 devices aren't supported \- the status bar will overlay the menu. This can be fixed if someone can give me the `Build.MODEL` value for the N7
* Better styling
* Slide\-to\-close fly\-in menu
* \(Maybe\) a "slide-under" fly\-in menu, where the Activity slides over to reveal the fly-in menu

License
=======

Copyright 2012 Alex Curran, David Scott in parts

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.