let item = {};

let type1Properties = {
    fire: {minValue: 10, maxValue: 101, name: 'health regen'},
    water: {minValue: 10, maxValue: 101, name: 'shield'},
    air: {minValue: 10, maxValue: 101, name: 'shield regen'},
    earth: {minValue: 10, maxValue: 101, name: 'health'},
};

let secondTierValueBonus = 50;

let type2Properties = {
    fire: {minValue: 10, maxValue: 101, name: 'stamina regen'},
    water: {minValue: 10, maxValue: 101, name: 'reserve'},
    air: {minValue: 10, maxValue: 101, name: 'reserve regen'},
    earth: {minValue: 10, maxValue: 101, name: 'stamina'},
};

let additionalGlowValueBonus = .1;

let createPropertyType1 = (source, bonus, maxMult) => {
    let property = type1Properties[source];
    return {name: property.name, value: bonus + randInt(property.minValue, property.maxValue * maxMult), source: source};
};

let createPropertyType2 = (source, maxMult) => {
    let property = type2Properties[source];
    return {name: property.name, value: randInt(property.minValue, property.maxValue * maxMult * item.enchantability / 100), source: source};
};

let createBaseProperty = (elements) => {
    if (elements.length === 1)
        return createPropertyType1(elements[0], 0, 1);

    else {
        let index = randInt(0, 2);
        return createPropertyType1(elements[index], secondTierValueBonus, 1);
    }
};

let createPrimaryProperty = (glows) => {
    let index = randInt(0, 3);
    let elements = glows[index].elements;

    if (elements.length === 1)
        return createPropertyType1(elements[0], 0, .5);

    else if (elements[0] === elements[1])
        return createPropertyType1(elements[0], 0, 1);

    else {
        let index = randInt(0, 2);
        if (item.step === 2)
            if (item.property[1].source === elements[0])
                index = 1;
            else if (item.property[1].source === elements[1])
                index = 0;

        return createPropertyType1(elements[index], 0, .5);
    }
};

let createSecondaryProperty = (glows) => {
    let index = randInt(0, glows.length);
    let elements = glows[index].elements;

    index = randInt(0, elements.length);
    if (item.step === 4 && elements.length > 1)
        if (item.property[3].source === elements[0])
            index = 1;
        else if (item.property[3].source === elements[1])
            index = 0;

    let multiply = 1 + ((glows.length - 1) * additionalGlowValueBonus);

    return createPropertyType2(elements[index], multiply);
};

let decreaseEnchantability = (amount) => {
    item.enchantability -= amount;
    if (item.enchantability < 10)
        item.enchantability = 10;
};

let init = () => {
    stepReset();
};

let stepReset = () => {
    item.step = 0;
    item.enchantability = 100;
    item.property = [
        {name: '', value: 0},
        {name: '', value: 0},
        {name: '', value: 0},
        {name: '', value: 0},
        {name: '', value: 0},
        {name: '', value: 0},
        {name: '', value: 0}];
    controller.updateProperties(item);
};

let stepBase = () => {
    if (item.step !== 0) {
        controller.setHelpText('Item must have no properties');
        return;
    }

    let glows = controller.getGlows();
    if (glows.length !== 1) {
        controller.setHelpText('Exactly 1 glow must be selected');
        return;
    }

    item.property[0] = createBaseProperty(glows[0].elements);

    item.step++;
    controller.updateProperties(item);
};

let stepBaseReset = () => {
    if (item.step !== 1) {
        controller.setHelpText('Item must have 1 property');
        return;
    }

    stepReset();
};

let stepPrimary = () => {
    if (item.step !== 1 && item.step !== 2) {
        controller.setHelpText('Item must have 1 or 2 properties');
        return;
    }

    let glows = controller.getGlows();

    if (glows.length !== 3) {
        controller.setHelpText('Exactly 3 glow must be selected');
        return;
    }

    if (item.step === 2) {
        let repeat = _.find(glows, (glow) => {
            let elements = glow.elements;
            return (elements.length === 1 || elements[0] === elements[1]) && item.property[1].source === elements[0]
        });
        if (repeat) {
            controller.setHelpText('Item already has a primary property of ' + item.property[1].source);
            return;
        }
    }

    item.property[item.step] = createPrimaryProperty(glows);

    item.step++;
    controller.updateProperties(item);
};

let stepPrimaryReset = () => {
    if (item.step !== 2 && item.step !== 3) {
        controller.setHelpText('Item must have 2 or 3 properties');
        return;
    }

    item.property[1].value = 0;
    item.property[2].value = 0;
    decreaseEnchantability(5);

    item.step = 1;
    controller.updateProperties(item);
};

let stepSecondary = () => {
    if (item.step !== 3 && item.step !== 4) {
        controller.setHelpText('Item must have 3 or 4 properties');
        return;
    }

    let glows = controller.getGlows();

    if (glows.length === 0) {
        controller.setHelpText('At least 1 glow must be selected');
        return;
    }

    if (item.step === 4) {
        let repeat = _.find(glows, (glow) => {
            let elements = glow.elements;
            return (elements.length === 1 || elements[0] === elements[1]) && item.property[3].source === elements[0]
        });
        if (repeat) {
            controller.setHelpText('Item already has a primary property of ' + item.property[3].source);
            return;
        }
    }

    item.property[item.step] = createSecondaryProperty(glows);

    item.step++;
    controller.updateProperties(item);
};

let stepSecondaryReset = () => {
    if (item.step !== 4 && item.step !== 5) {
        controller.setHelpText('Item must have 4 or 5 properties');
        return;
    }

    item.property[3].value = 0;
    item.property[4].value = 0;
    decreaseEnchantability(10);

    item.step = 3;
    controller.updateProperties(item);
};

let stepEnhance = () => {

};

let stepEnhanceReset = () => {

};

window.onload = init;

// enhance
// initial imbue step (durability, enchantability, free reset, higher primary rolls)
// item types