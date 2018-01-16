let item = {};

let baseProperties = {
    fire: {minValue: 10, maxValue: 101, name: 'health regen'},
    water: {minValue: 10, maxValue: 101, name: 'shield'},
    air: {minValue: 10, maxValue: 101, name: 'shield regen'},
    earth: {minValue: 10, maxValue: 101, name: 'health'},
};

let valueBonus = 50;

let createItemProperty = (source, bonus, maxMult) => {
    let property = baseProperties[source];
    return {name: property.name, value: bonus + randInt(property.minValue, property.maxValue * maxMult), source: source};
};

let createBaseProperty = (elements) => {
    if (elements.length === 1)
        return createItemProperty(elements[0], 0, 1);

    else {
        let index = randInt(0, 2);
        return createItemProperty(elements[index], valueBonus, 1);
    }
};

let createPrimaryProperty = (glows) => {
    let index = randInt(0, 3);
    let elements = glows[index].elements;

    if (elements.length === 1)
        return createItemProperty(elements[0], 0, .5);

    else if (elements[0] === elements[1])
        return createItemProperty(elements[0], 0, 1);

    else {
        let index = randInt(0, 2);
        if (item.step === 2)
            if (item.property[1].source === elements[0])
                index = 1;
            else if (item.property[1].source === elements[1])
                index = 0;

        return createItemProperty(elements[index], 0, .5);
    }
};

let init = () => {
    resetItem();
};

let resetItem = () => {
    item.step = 0;
    item.property = [
        {name: '', value: 0},
        {name: '', value: 0},
        {name: '', value: 0},
        {name: '', value: 0},
        {name: '', value: 0},
        {name: '', value: 0},
        {name: '', value: 0}];
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

    item.step++;
    controller.updateProperties(item);
};

let stepPrimary = () => {
    if (item.step !== 1 && item.step !== 2)
        return;

    let glows = controller.getGlows();

    if (glows.length !== 3)
        return;

    if (item.step === 2) {
        let repeat = _.find(glows, (glow) => {
            let elements = glow.elements;
            return (elements.length === 1 || elements[0] === elements[1]) && item.property[1].source === elements[0]
        });
        if (repeat)
            return;
    }

    item.property[item.step] = createPrimaryProperty(glows);

    item.step++;
    controller.updateProperties(item);
};

let stepSecondary = () => {

};

let stepEnhance = () => {

};

window.onload = init;

/*
 item type determines secondary attributes possible
 base gives rolls 10-100 based on which glow for tier 1, 60-150 for tier 2 or hybrid (chosen randomly)
 primary 1 and 2 rolls 10-50 (1st glow) or 10-100 (2nd glow) or 10-50 or random glow (hybrid glow) of randomly chosen from 3 glows
 primary clear removes primary enchants and reduces enchant-ability by 5 
 secondary 1 and 2 rolls rolls of secondary attributes, possible outcomes based on which glows, value range based on # of glows  
 secondary clear removes secondary enchants and reduces enchant-ability by 10 
 */