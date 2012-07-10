This is an Android Translation Helper (or differ).

As input parameters takes a original res/values file (typically English)
and res/values-<code> file as file with translation.
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
directory processing - as first (and only one) argument specify the resource
directory where detect the original English text (values), enumerate all xml
files and compare each with specific translation one in values-<country code>
directory