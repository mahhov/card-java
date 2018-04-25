let half = array => {
    let length = array.length;
    if (length <= 1)
        return array;
    if (length === 2) {
        if (array[0] > array[1])
            flip(array);
        return array;
    } else {
        let [first, second] = split(array);
        first = half(first.slice());
        second = half(second.slice());
        let f = 0;
        let s = 0;
        let merged = [];
        for (let i = 0; i < length; i++)
            if (f === first.length)
                merged.push(second[s++])
            else if (s === second.length)
                merged.push(first[f++])
            else if (first[f] <= second[s])
                merged.push(first[f++]);
            else
                merged.push(second[s++]);
        return merged;
    }
};

let flip = array => {
    let t = array[0];
    array[0] = array[1];
    array[1] = t;
};

let split = array => {
    let middle = parseInt(array.length / 2);
    let first = array.splice(0, middle);
    return [first, array];
};

module.exports = half;
