Android Translation Helper
==========================

This is an Android Translation Helper (or differ). This tool is designed
to help a translating partialy translated files for Android platform.
It takes a source (original file - usualy English file from
res/values directory), parse it, take the string elements and compare
it with translation file (usualy in some of res/values-* directory).

Input parameters can be:
* [original file] [translation file]
- concrete string files to be compared
* [translation directory]
- in this situation the tool takes all files in this directory and compare
it with file from original directory (assume the res/values)
* [translation file]
- tool takes a translation file and compare it with original file from
computed original directory (assume the res/values directory)

After short run it will create a simple report with
- what is included in original file but not in translation file
- what is included in translation file but not in original file

Supported elements: <array>, <integer-array>, <string-array>,
<plurals>, <string>

For simple string item, the value is displayed. For other items, number
of items is compared and if differ, number of original items and
translation items are displayed.

Feel free to use it under GNU GPL.
Keltek

TODO: make a gui ;)
