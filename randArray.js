const merge = require('./merge');
const insert = require('./insert');
const quick = require('./quick');

let getRand = max => Math.floor(Math.random() * max);

let len = getRand(10) + 10;
let input = [];
for (let i = 0; i < len; i++)
    input.push(getRand(100));

console.log(input);
console.log('merge');
console.log(merge(input.slice()));
console.log('insert');
console.log(insert(input.slice()));
console.log('quick');
console.log(quick(input.slice()));
console.log('input');
console.log(input);
