let item = {};

let baseProperties = {
    fire: {minValue: 1, maxValue: 101, name: 'health regen'},
    water: {minValue: 1, maxValue: 101, name: 'shield'},
    air: {minValue: 1, maxValue: 101, name: 'shield regen'},
    earth: {minValue: 1, maxValue: 101, name: 'health'},
};

let createBaseProperty = (elements) => {
    if (elements.length === 1) {
        let property = baseProperties[elements[0]];
        return {name: property.name, value: randInt(property.minValue, property.maxValue)};

    } else {
        let index = randInt(0, 2);
        let property = baseProperties[elements[index]];
        return {name: property.name, value: 25 + randInt(property.minValue, property.maxValue)};
    }
};

let init = () => {
    resetItem();
};

let resetItem = () => {
    item.step = 0;
    item.property = [
        {name: '-', value: 0},
        {name: '-', value: 0},
        {name: '-', value: 0},
        {name: '-', value: 0},
        {name: '-', value: 0},
        {name: '-', value: 0},
        {name: '-', value: 0}];
};

let stepItem = () => {
    resetItem();
    controller.updateProperties(item);
};

let stepBase = () => {
    if (item.step !== 0)
        return;

    let glows = controller.getGlows();

    if (glows.length > 1)
        return;

    if (glows.length === 0)
        item.property[0].name = 'none';

    else
        item.property[0] = createBaseProperty(glows[0].elements);

    item.step = 1;
    controller.updateProperties(item);
};

let stepPrimary = () => {

};

let stepSecondary = () => {

};

let stepEnhance = () => {

};

window.onload = init;

/*
 item type determines secondary attributes possible
 base gives rolls 1-100 based on which glow
 primary 1 and 2 rolls 1-50 of randomly chosen from 3 glows
 primary clear removes primary enchants and reduces enchant-ability by 5 
 secondary 1 and 2 rolls rolls of secondary attributes, possible outcomes based on which glows, value range based on # of glows  
 secondary clear removes secondary enchants and reduces enchant-ability by 10 
 */