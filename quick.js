let quick = array => {
    let len = array.length;

    if (len < 2)
        return array;

    if (len === 2) {
        if (array[0] > array[1]) {
            t = array[0];
            array[0] = array[1];
            array[1] = t;
        }
        return array;
    }

    let p = parseInt(len / 2);
    let pivot = array[p];
    let small = [];
    let large = [];
    let same = [];
    array.forEach(v => {
        if (v === pivot)
            same.push(v)
        else if (v < pivot)
            small.push(v);
        else
            large.push(v);
    });

    small = quick(small);
    large = quick(large);

    return small.concat(same, large);
};

module.exports = quick;
