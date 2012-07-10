FlyInMenu
==========

Implementation of the emerging fly-in menu pattern for Android apps. This utilises a high-level modification of the application window to add the fly-in menu (FIM), which is loaded from a menu resource.

This implementation is based on RibbonMenu (Copyright David Scott), but much expanded. It includes support for SearchViews and custom views, improved animations (with two different styles) and is cleaner to use.


Usage
=====

Set your activity to override one of the FlyInMenuActivities (there is one for ActionBarSherlock users and one for the support library's FragmentActivity), you can load the FIM at any time by calling `loadFlyInMenu(..)`, after which you can attach custom views (`getFlyInMenu().setCustomView(..)`) or initalise a SearchView (`getFlyInMenu().enableSearchView()`).

The fly-in animation can be one of two options set using `setFlyInType(..)`. `FLY_IN_OVER_ACTIVITY` brings the fly-in menu over the Activity, obscuring it. `FLY_IN_WITH_ACTIVITY` pushes the Activity's contents to the right.


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