README

--------------------------------------------------------------------
* COPYRIGHT

Copyright (c) 2005-2006, AIOTrade Computing Co.

This program is free software; you can redistribute it
and/or modify it under the terms of the GNU General Public
License as published by the Free Software Foundation;
either version 2 of the License, or (at your option) any
later version.

This program is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE. See the GNU General Public License for more
details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place, Suite 330,
Boston, MA 02111-1307 USA

--------------------------------------------------------------------
* For User:

1. The user config files, which is locate at (from verion 1.0.3):
     
     # ${HOME} will be replaced by user home directory according to platform
     default_userdir="${HOME}/.${APPNAME}/1.0.3"
     default_mac_userdir="${HOME}/Library/Application Support/${APPNAME}/1.0.3"

   are not compatible with previous versions. You should not copy old user 
   config files to override new version's user config files.
   
   The user config files location can be changed by editing:
   /aiotrade/etc/aiotrade.conf

2. Due to a bug in Java 6.0rc or earlier, this application may throw
   exceptions sometime. Hope this will be fixed in Java 6.0 official
   release.

   The Java Runtime Environment can be set by editing: 
   /aiotrade/etc/aiotrade.conf
   uncomment the following line and change to your own:

     #jdkhome="/path/to/jdk"


3. The user manual is currently internet available only, you may get
   it at:
   http://aiotrade.com/manual_user/usermanual.html

--------------------------------------------------------------------
* For Developer:

AIOTrade Platform's source code is packaged as a NetBeans Module 
Suite project, to compile and run it, you should download the NetBeans
IDE 6.0 M3 or higher, and follow the steps:
   
   1. unzip the source zipped file to some where, such as: \aiotrade-src\ 
   2. Click menu 'File' -> 'Open Project...' and choose the root director
      of source files.
   3. On the opened projects list, Right click mouse on 'AIOTrade Platform'
      project, select 'Properties' from popup menu and choose 'Libaries' on
      the left pane, then check if the 'NetBeans Platform:' is set to the 
      IDE itself on the right pane.

Notice: This project is still features focused (not API focused yet), the 
architecture may not be stable enough for you. The APIs may be changed
without notice. It's better to wait for untill the release version 2.0 :-)

Release 1.0.3a has been tested on NetBeans IDE 6.0 M3 and Java 5.0. 
