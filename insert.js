let insert = array => {
    for (let from = 0; from < array.length; from++) {
        let to = from;
        while (to > 0 && array[from] < array[to - 1]) to--;
        let t = array[from];
        for (let i = from; i > to; i--)
            array[i] = array[i - 1];
        array[to] = t;
    }
    return array;
};

module.exports = insert;
