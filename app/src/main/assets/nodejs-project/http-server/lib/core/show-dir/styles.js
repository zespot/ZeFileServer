'use strict';

const icons = require('./icons.json');

const IMG_SIZE = 16;

let css = `i.icon { display: block; height: ${IMG_SIZE}px; width: ${IMG_SIZE}px; }\n`;
css += 'table tr { white-space: nowrap; }\n';
css += 'td.perms {}\n';
css += 'td.file-size { text-align: right; padding-left: 1em; }\n';
css += 'td.display-name { padding-left: 1em; }\n';
css += 'h1 {font-size: 2em;  }\n';
css += '@media only screen and (max-width: 600px) {h1 { font-size: 1.2em; }}\n';
css += 'h2 {font-size: 1.5em;  }\n';
css += '@media only screen and (max-width: 600px) {h2 { font-size: 1em; }}\n';
css += '.download {  \n';
css += `  background-image: url("data:image/jpg;base64,${icons["download"]}");\n`;
css += 'background-repeat: no-repeat;'
css += 'display: inline-block;    content:"";   background-size: 100% 100%; width: 16px;   height: 16px;   ;vertical-align:middle;'
css += 'color:#fff  ; ;'
css += '}\n\n';


css+= '.bandeau {border: 5px solid  #32a1ce;display: flex; align-items: center;}\n'

css += 'a {\tword-break: break-all;\n' +
    '\twhite-space: normal;}'

Object.keys(icons).forEach((key) => {
  css += `i.icon-${key} {\n`;
  css += `  background-image: url("data:image/jpg;base64,${icons[key]}");\n`;
  css += 'display: inline-block;    content:"";   background-size: 100% 100%; width: 16px;   height: 16px;   ;vertical-align:middle;'
  css += '}\n\n';
});


exports.icons = icons;
exports.css = css;
