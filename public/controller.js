let controller = (() => {
    let updateProperties = (item) => {
        setHelpText('');

        itemType.innerText = item.type;

        enchantability.innerText = item.enchantability;

        let properties = [property1, property2, property3, property4, property5, property6];
        _.each(properties, (property, index) => {
            if (item.property[index].value === 0)
                property.innerText = '';
            else
                property.innerText = item.property[index].name + ' ' + item.property[index].value;
        });
    };

    let setHelpText = (text) => {
        helpText.innerText = text;
    };

    let getGlows = () => {
        let glows = [];

        glows.push({name: 'Fire1', elements: ['fire'], value: glowFire1.checked});
        glows.push({name: 'Water1', elements: ['water'], value: glowWater1.checked});
        glows.push({name: 'Air1', elements: ['air'], value: glowAir1.checked});
        glows.push({name: 'Earth1', elements: ['earth'], value: glowEarth1.checked});
        glows.push({name: 'FireWater', elements: ['fire', 'water'], value: glowFireWater.checked});
        glows.push({name: 'FireAir', elements: ['fire', 'air'], value: glowFireAir.checked});
        glows.push({name: 'FireEarth', elements: ['fire', 'earth'], value: glowFireEarth.checked});
        glows.push({name: 'WaterAir', elements: ['water', 'air'], value: glowWaterAir.checked});
        glows.push({name: 'WaterEarth', elements: ['water', 'earth'], value: glowWaterEarth.checked});
        glows.push({name: 'AirEarth', elements: ['air', 'earth'], value: glowAirEarth.checked});
        glows.push({name: 'Fire2', elements: ['fire', 'fire'], value: glowFire2.checked});
        glows.push({name: 'Water2', elements: ['water', 'water'], value: glowWater2.checked});
        glows.push({name: 'Air2', elements: ['air', 'air'], value: glowAir2.checked});
        glows.push({name: 'Earth2', elements: ['earth', 'earth'], value: glowEarth2.checked});

        glows = _.filter(glows, (glow) => {
            return glow.value;
        });

        return glows;
    };

    let getItemTypeText = () => {
        return itemTypeSelect.selectedOptions[0] && itemTypeSelect.selectedOptions[0].value;
    };

    return {
        updateProperties: updateProperties,
        setHelpText: setHelpText,
        getGlows: getGlows,
        getItemTypeText: getItemTypeText,
    };
})();
