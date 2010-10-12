var counter = 0;
var	mouseX = 0;
var	mouseY = 0;

function coolTip(parent, id, content, width, height, par, color, theme) {
    if($(id) == null) {
        createCoolTip(parent, id, content, width, height, par, color, theme);
        showCoolTip(id);
    } else if(Element.visible(id)){
        hideCoolTip(id);
    } else if(!Element.visible(id)) {
        showCoolTip(id);
    }
}

function showCoolTip(id) {
    $(id).style.zIndex = ++counter;
    Effect.Appear(id, { duration: 0.5 });
}

function hideCoolTip(id){
    Effect.Fade(id, { duration: .5 });
}

function createCoolTip(parent, id, content, width, height, par, color, theme) {
    var baseDiv = document.createElement('div');
    baseDiv.setAttribute('id', id);
    baseDiv.setAttribute('class', 'base-layer');
    baseDiv.setAttribute('style', 'display: none; left: '+ (mouseX-width)+'px; top: '+mouseY+'px; z-index: '+(++counter)+';');

    var row1Div = document.createElement('div');
    row1Div.setAttribute('id', 'r1'+id);
    row1Div.setAttribute('class', 'table-row');

    var cell11Div = document.createElement('div');
    cell11Div.setAttribute('id', 'c11'+id);
    cell11Div.setAttribute('class', 'c11');
    cell11Div.style.width  = par+'px';
    cell11Div.style.height = par+'px';
    cell11Div.style.backgroundImage = "url('../img/cooltip/"+theme+"/11.png')";

    var cell12Div = document.createElement('div');
    cell12Div.setAttribute('id', 'c12'+id);
    cell12Div.setAttribute('class', 'c12');
    cell12Div.setAttribute('style', 'width: '+(width-2*par)+'px');
    cell12Div.style.height = par+'px';
    cell12Div.style.backgroundImage = "url('../img/cooltip/"+theme+"/12.png')";

    var cell13Div = document.createElement('div');
    cell13Div.setAttribute('id', 'c13'+id);
    cell13Div.setAttribute('class', 'c13');
    cell13Div.style.width  = par+'px';
    cell13Div.style.height = par+'px';
    cell13Div.style.backgroundImage = "url('../img/cooltip/"+theme+"/13.png')";

    var row2Div = document.createElement('div');
    row2Div.setAttribute('id', 'r2'+id);
    row2Div.setAttribute('class', 'table-row');

    var cell21Div = document.createElement('div');
    cell21Div.setAttribute('id', 'c21'+id);
    cell21Div.setAttribute('class', 'c21');
    cell21Div.setAttribute('style', 'height: '+height+'px');
    cell21Div.style.width = par+'px';
    cell21Div.style.backgroundImage = "url('../img/cooltip/"+theme+"/21.png')";

    var cell22Div = document.createElement('div');
    cell22Div.setAttribute('id', 'c22'+id);
    cell22Div.setAttribute('class', 'c22');
    cell22Div.setAttribute('style', 'background-color: '+color+'; width: '+(width-2*par)+'px; height: '+height+'px');

    var cell23Div = document.createElement('div');
    cell23Div.setAttribute('id', 'c23'+id);
    cell23Div.setAttribute('class', 'c23');
    cell23Div.setAttribute('style', 'height: '+height+'px');
    cell23Div.style.width = par+'px';
    cell23Div.style.backgroundImage = "url('../img/cooltip/"+theme+"/23.png')";

    var row3Div = document.createElement('div');
    row3Div.setAttribute('id', 'r3'+id);
    row3Div.setAttribute('class', 'table-row');

    var cell31Div = document.createElement('div');
    cell31Div.setAttribute('id', 'c31'+id);
    cell31Div.setAttribute('class', 'c31');
    cell31Div.style.width  = par+'px';
    cell31Div.style.height = par+'px';
    cell31Div.style.backgroundImage = "url('../img/cooltip/"+theme+"/31.png')";

    var cell32Div = document.createElement('div');
    cell32Div.setAttribute('id', 'c32'+id);
    cell32Div.setAttribute('class', 'c32');
    cell32Div.setAttribute('style', 'width: '+(width-2*par)+'px');
    cell32Div.style.height = par+'px';
    cell32Div.style.backgroundImage = "url('../img/cooltip/"+theme+"/32.png')";

    var cell33Div = document.createElement('div');
    cell33Div.setAttribute('id', 'c33'+id);
    cell33Div.setAttribute('class', 'c33');
    cell33Div.style.width  = par+'px';
    cell33Div.style.height = par+'px';
    cell33Div.style.backgroundImage = "url('../img/cooltip/"+theme+"/33.png')";

    var innerTable = document.createElement('table');
    innerTable.setAttribute('id', 't'+id);

    var innerTr = document.createElement('tr');
    var innerTd = document.createElement('td');

    var coord = alertSize();
    var coordX = coord[0];
    var coordY = coord[1];

    innerTd.innerHTML = content + '['+coordX+','+coordY+']';
    innerTr.appendChild(innerTd);
    innerTable.appendChild(innerTr);
    cell22Div.appendChild(innerTable);

    row1Div.appendChild(cell11Div);
    row1Div.appendChild(cell12Div);
    row1Div.appendChild(cell13Div);

    row2Div.appendChild(cell21Div);
    row2Div.appendChild(cell22Div);
    row2Div.appendChild(cell23Div);

    row3Div.appendChild(cell31Div);
    row3Div.appendChild(cell32Div);
    row3Div.appendChild(cell33Div);

    baseDiv.appendChild(row1Div);
    baseDiv.appendChild(row2Div);
    baseDiv.appendChild(row3Div);

    parent.appendChild(baseDiv);
}

function alertSize() {
  var myWidth = 0, myHeight = 0;
  if( typeof(window.innerWidth) == 'number' ) {
    //Non-IE
    myWidth = window.innerWidth;
    myHeight = window.innerHeight;
  } else if(document.documentElement && (document.documentElement.clientWidth || document.documentElement.clientHeight)) {
    //IE 6+ in 'standards compliant mode'
    myWidth = document.documentElement.clientWidth;
    myHeight = document.documentElement.clientHeight;
  } else if(document.body && (document.body.clientWidth || document.body.clientHeight)) {
    //IE 4 compatible
    myWidth = document.body.clientWidth;
    myHeight = document.body.clientHeight;
  }
  return [myWidth , myHeight];
}

function getScrollXY() {
  var scrOfX = 0, scrOfY = 0;
  if(typeof(window.pageYOffset) == 'number') {
    //Netscape compliant
    scrOfY = window.pageYOffset;
    scrOfX = window.pageXOffset;
  } else if(document.body && (document.body.scrollLeft || document.body.scrollTop)) {
    //DOM compliant
    scrOfY = document.body.scrollTop;
    scrOfX = document.body.scrollLeft;
  } else if(document.documentElement && (document.documentElement.scrollLeft || document.documentElement.scrollTop)) {
    //IE6 standards compliant mode
    scrOfY = document.documentElement.scrollTop;
    scrOfX = document.documentElement.scrollLeft;
  }
  return [scrOfX, scrOfY];
}

function getCoords(e){
	mouseX = Event.pointerX(e);
	mouseY = Event.pointerY(e);
	Event.stop(e);
}
