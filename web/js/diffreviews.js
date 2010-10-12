var score = 0.0;
var paramStars = '';

function getCurrentUrl() {
    return window.location;
}

function init() {
    $('stars').value = "";
    $('stars').observe('keypress', respondToEnter);
    $('stars').observe('keyup', respondToKeyUp);
    Event.observe(document, 'mousemove', getCoords);
    setTimeout(setFocusOnInput, 100);
}

function setFocusOnInput() {
    $('stars').focus();
}

function respondToEnter(event) {
  if(event.keyCode == Event.KEY_RETURN) {
      getScore();
  }
}

function respondToKeyUp(event) {
    if(event.keyCode != Event.KEY_RETURN) {
        updateStars(event);
    }
    if(!Element.visible('summary')) {
        Effect.SlideDown('summary', { duration: 4.0 });
    }
    if(!Element.visible('settings') && !Element.visible('history')) {
        Effect.SlideDown('settings', { duration: 4.0 });
    }
}

function updateStars() {
    paramStars = '';
    for(var i=5; i>0; i--) {
        var localStars = getStarValue($F('stars'), i);
        $(i+'stars').update(localStars);
        paramStars += localStars + '_';
    }
}

function getStarValue(number, value) {
    var starsArray = number.match(/\d+/g);
    if(value == 5) {if(starsArray[0] != null) {return stripNumber(starsArray[0]);} else {return 0;}}
    if(value == 4) {if(starsArray[1] != null) {return stripNumber(starsArray[1]);} else {return 0;}}
    if(value == 3) {if(starsArray[2] != null) {return stripNumber(starsArray[2]);} else {return 0;}}
    if(value == 2) {if(starsArray[3] != null) {return stripNumber(starsArray[3]);} else {return 0;}}
    if(value == 1) {if(starsArray[4] != null) {return stripNumber(starsArray[4]);} else {return 0;}}
    return 0;
}

function stripNumber(number) {
    if(number > 9999) {
        var exced = (number.length - 4);
        var tens = 1;
        for(var i=0; i<exced; i++) {
            tens = tens * 10;
        }
        var rest = Math.floor(number / tens);
        return rest;
    }
    return number;
}

function getScore() {
    $('btnScore').disable();
    $('message').textContent = 'processing...';
    var basehost = getCurrentUrl();
    var url = basehost + "ReviewService";
    if(!isNumeric()) {
        paramStars = escape($F('stars'));
    }
    var pars = 'stars=' + paramStars;
    pars = pars + '&cfd=' + $F('confidence');
    pars = pars + '&mtd=' + $F('method');
    pars = pars + '&dec=' + $F('decimals');
    var myAjax = new Ajax.Request(url,
                                  {method: 'post',
                                   onSuccess  : onRequestSuccess,
                                   onFailure  : onRequestFailure,
                                   onComplete : onRequestComplete,
                                   parameters : pars});
}

function isNumeric() {
    var input = $F('stars');
    var actual   = input.substring(0,21);
    var expected = 'http://www.newegg.com';
    if(actual == expected) {
        return false;
    }
    var itemLength = input.length;
    var isValidLength = false;
    if(itemLength == 15) {
        isValidLength = true;
    }
    if(isValidLength) {
        var indexSpace = input.indexOf(' ');
        var isNoSpace = (indexSpace < 0) ? true : false;
        if(isNoSpace) {
            var firstChar = input.charAt(0);
            var isFirstCharN = (firstChar == 'N') ? true : false;
            if(isFirstCharN) {
                return false;
            }
        }
    }
    return true;
}

function getSummary(){
    new Effect.SlideUp('summary');
}

function onRequestSuccess(transport) {
    $('btnScore').enable();
    if(transport != null) {
        var content = transport.responseText;
        var dataIn = content.evalJSON();
        score = dataIn.score;
        var decimals = dataIn.decimals;
        $('score').update(getFormattedNumber(score, decimals));
        var info = dataIn.message;
        if(info != null) {
            $('message').innerHTML = " ";
        }
        if(!Element.visible('score_table')) {
            Effect.SlideDown('score_table', { duration: 0.3 });
        }
        $('5stars').update(getNumberOfStars(info, "5"));
        $('4stars').update(getNumberOfStars(info, "4"));
        $('3stars').update(getNumberOfStars(info, "3"));
        $('2stars').update(getNumberOfStars(info, "2"));
        $('1stars').update(getNumberOfStars(info, "1"));
        addScoreToHistory();
    }
}

function getNumberOfStars(info, star) {
    var stars = info.split(" ");
    if(star == "5") {return stars[0];}
    if(star == "4") {return stars[1];}
    if(star == "3") {return stars[2];}
    if(star == "2") {return stars[3];}
    if(star == "1") {return stars[4];}
    
    return 0;
}

function formatScore() {
    $('score').update(getFormattedNumber(score, $F('decimals')));
}

function getFormattedNumber(amount, decimals) {
	var i = parseFloat(amount);
	if(isNaN(i)) {
            i = 0.00;
        }
	var minus = '';
	if(i < 0) {
            minus = '-';
        }
	i = Math.abs(i);
	s = new String(i);
	if(s.indexOf('.') < 0) {
            s += '.';
            for(var m=0; m<decimals; m++) {
                s += '0';
            }
        } else {
            var decs = parseFloat(decimals);
            var scoreLength = 1 + s.indexOf('.') + decs;
            s = s.substring(0, scoreLength);
        }
	s = minus + s;
	return s;
}

function onRequestFailure(transport) {
    $('message').textContent = 'Error connecting to server.';
    $('btnCompare').enable();
}

function onRequestComplete(transport) {
    // handle the response
}

function addScoreToHistory() {
  var table= $('past_score_table');
  for(var i=7; i > 0; i--) {
    for(var j=0; j< 7; j++) {
        $(table.rows[i].cells[j]).update(table.rows[i-1].cells[j].innerText);
    }
  }
  $(table.rows[0].cells[0]).update(getCurrentInput(5));
  $(table.rows[0].cells[1]).update(getCurrentInput(4));
  $(table.rows[0].cells[2]).update(getCurrentInput(3));
  $(table.rows[0].cells[3]).update(getCurrentInput(2));
  $(table.rows[0].cells[4]).update(getCurrentInput(1));
  $(table.rows[0].cells[5]).update('-');
  $(table.rows[0].cells[6]).update(getCurrentScore());
}

function getCurrentInput(m) {
    var crtStar = m+'stars';
    var val = $(crtStar).innerText;
    return val;
}

function getCurrentScore() {
    return $('score').innerText;
}

function toggleSettingHistory() {
    if(Element.visible('settings')) {
        Effect.SlideUp('settings', { duration: 0.5 });
        Effect.SlideDown('history', { duration: 0.5 });
        $('settop').src="img/setting_topz.png";
    } else if(Element.visible('history')) {
        Effect.SlideUp('history', { duration: 0.5 });
        Effect.SlideDown('settings', { duration: 0.5 });
        $('settop').src="img/setting_top.png";
    } else if(!Element.visible('settings') &&
            !Element.visible('settings')) {
        Effect.SlideDown('settings', { duration: 0.5 });
        $('settop').src="img/setting_top.png";
    }
    return false;
}

function closeHelp() {
    if($('tt') != null && Element.visible('tt')) {
        tooltip.hide();
    }
}

function closeAbout() {
    if($('ct') != null && Element.visible('ct')) {
        cooltip.hide();
    }
}

function help() {
    if($('tt') != null && Element.visible('tt')) {
        tooltip.hide();
    } else {
        tooltip.show('<table width="100%">'+
                     '  <tr>' +
                     '    <td>' +
                     '      <b>Help</b>' +
                     '    </td>' +
                     '    <td align="right">' +
                     '      <img src="../img/close.png" style="cursor:pointer" onClick="closeHelp();"><br />' +
                     '    </td>' +
                     '  </tr>' +
                     '  <tr>' +
                     '    <td colspan="2">' +
                     '      <hr />' +
                     '    </td>' +
                     '  </tr>' +
                     '  <tr>' +
                     '    <td colspan="2">' +
                     'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus vitae ante vel tortor imperdiet hendrerit. '+
                     'Maecenas blandit lorem non leo placerat sed pharetra metus facilisis. Donec et mauris lacus. Nullam ipsum urna,'+
                     ' feugiat non iaculis non, imperdiet nec ante. Fusce feugiat lorem vel augue fermentum laoreet. Mauris aliquam '+
                     'feugiat eros, eu viverra metus mattis ut. Quisque a pharetra eros. In hac habitasse platea dictumst. Maecenas '+
                     'sed convallis odio. Nam in ligula nunc. Proin ullamcorper orci nec felis bibendum pretium. Sed iaculis, urna '+
                     'dapibus vestibulum vulputate, libero elit porta diam, ut tempor ante libero at ipsum. Integer pharetra eros '+
                     'convallis ipsum venenatis id tincidunt enim varius. Proin congue nisl ut orci porta dapibus. Etiam adipiscing '+
                     'feugiat metus sed malesuada. Nam in risus purus, quis ultrices felis. Ut dictum metus eu lorem lobortis vulputate.'+
                     ' Vestibulum vel lorem id est mollis aliquam.' +
                     '    </td>' +
                     '  </tr>' +
                     '  <tr>' +
                     '  <tr>' +
                     '    <td>' +
                     '      &nbsp;' +
                     '    </td>' +
                     '    <td align="right">' +
                     '      &nbsp;' +
                     '    </td>' +
                     '  </tr>' +
                     '  <tr>' +
                     '</table>'
                    , 400, 0);
    }
}

function about() {
    if($('ct') != null && Element.visible('ct')) {
        cooltip.hide();
    } else {
        cooltip.show('<table width="100%" bgcolor=#FFE9BA>'+
                     '  <tr>' +
                     '    <td>' +
                     '      <b>About</b>' +
                     '    </td>' +
                     '    <td align="right">' +
                     '      <img src="../img/close.png" style="cursor:pointer" onClick="closeAbout();"><br />' +
                     '    </td>' +
                     '  </tr>' +
                     '  <tr>' +
                     '    <td colspan="2">' +
                     '      <hr />' +
                     '    </td>' +
                     '  </tr>' +
                     '  <tr>' +
                     '    <td colspan="2">' +
                     'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus vitae ante vel tortor imperdiet hendrerit. '+
                     'Maecenas blandit lorem non leo placerat sed pharetra metus facilisis. Donec et mauris lacus. Nullam ipsum urna,'+
                     ' feugiat non iaculis non, imperdiet nec ante. Fusce feugiat lorem vel augue fermentum laoreet. Mauris aliquam '+
                     'feugiat eros, eu viverra metus mattis ut. Quisque a pharetra eros. In hac habitasse platea dictumst. Maecenas '+
                     'sed convallis odio. Nam in ligula nunc. Proin ullamcorper orci nec felis bibendum pretium. Sed iaculis, urna '+
                     'dapibus vestibulum vulputate, libero elit porta diam, ut tempor ante libero at ipsum. Integer pharetra eros '+
                     'convallis ipsum venenatis id tincidunt enim varius. Proin congue nisl ut orci porta dapibus. Etiam adipiscing '+
                     'feugiat metus sed malesuada. Nam in risus purus, quis ultrices felis. Ut dictum metus eu lorem lobortis vulputate.'+
                     ' Vestibulum vel lorem id est mollis aliquam.' +
                     '    </td>' +
                     '  </tr>' +
                     '  <tr>' +
                     '  <tr>' +
                     '    <td>' +
                     '      &nbsp;' +
                     '    </td>' +
                     '    <td align="right">' +
                     '      &nbsp;' +
                     '    </td>' +
                     '  </tr>' +
                     '  <tr>' +
                     '</table>'
                    , 500, -30);
    }
}

var tooltip=function(){
	var id = 'tt';
	var top = 3;
	var left = 3;
	var maxw = 500;
	var speed = 10;
	var timer = 20;
	var endalpha = 95;
	var alpha = 0;
	var tt,t,c,b,h;
        var crtWidth = 0;
        var shift = 0;
	var ie = document.all ? true : false;
	return{
		show:function(v, w, z){
			if(tt == null){
				tt = document.createElement('div');
				tt.setAttribute('id',id);
				t = document.createElement('div');
				t.setAttribute('id',id + 'top');
				c = document.createElement('div');
				c.setAttribute('id',id + 'cont');
				b = document.createElement('div');
				b.setAttribute('id',id + 'bot');
				tt.appendChild(t);
				tt.appendChild(c);
				tt.appendChild(b);
				document.body.appendChild(tt);
				tt.style.opacity = 0;
				tt.style.filter = 'alpha(opacity=0)';
				document.onmousemove = this.pos;
			}
                        shift = z;
			tt.style.display = 'block';
			c.innerHTML = v;
			tt.style.width = w ? w + 'px' : 'auto';
			if(!w && ie){
				t.style.display = 'none';
				b.style.display = 'none';
				tt.style.width = tt.offsetWidth;
				t.style.display = 'block';
				b.style.display = 'block';
			}
                        crtWidth = w;
			if(tt.offsetWidth > maxw) {
                            tt.style.width = maxw + 'px';
                            crtWidth = maxw;
                        }
			h = parseInt(tt.offsetHeight) + top;
			clearInterval(tt.timer);
			tt.timer = setInterval(function(){tooltip.fade(1)},timer);
		},
		pos:function(){
                        var myWidth = ie ? document.documentElement.clientWidth : window.innerWidth;
			tt.style.top = (144 - tt.style.height) + 'px';
			tt.style.left = (myWidth - crtWidth + shift - 10) + 'px';
		},
		fade:function(d){
			var a = alpha;
			if((a != endalpha && d == 1) || (a != 0 && d == -1)) {
				var i = speed;
				if(endalpha - a < speed && d == 1) {
					i = endalpha - a;
				} else if(alpha < speed && d == -1) {
					i = a;
				}
				alpha = a + (i * d);
				tt.style.opacity = alpha * .01;
				tt.style.filter = 'alpha(opacity=' + alpha + ')';
			} else {
				clearInterval(tt.timer);
				if(d == -1){tt.style.display = 'none'}
			}
		},
		hide:function(){
			clearInterval(tt.timer);
			tt.timer = setInterval(function(){tooltip.fade(-1)},timer);
		}
	};
}();

var cooltip=function(){
	var id = 'ct';
	var top = 3;
	var left = 3;
	var maxw = 500;
	var speed = 10;
	var timer = 20;
	var endalpha = 95;
	var alpha = 0;
	var ct,t,tl,tc,tr,c,cl,cc,cr,b,bl,bc,br,h;
        var crtWidth = 0;
        var crtHeight = 144;
        var shift = 0;
	var ie = document.all ? true : false;
	return{
		show:function(v, w, z){
			if(ct == null){
				ct = document.createElement('div');
				ct.setAttribute('id',id);

                                t = document.createElement('div');
				t.setAttribute('id',id + 'top');
                                tl = document.createElement('div');
				tl.setAttribute('id',id + 'topleft');
                                tc = document.createElement('div');
				tc.setAttribute('id',id + 'topcenter');
                                tr = document.createElement('div');
				tr.setAttribute('id',id + 'topright');

                                c = document.createElement('div');
				c.setAttribute('id',id + 'center');
                                cl = document.createElement('div');
				cl.setAttribute('id',id + 'centerleft');
                                cc = document.createElement('div');
				cc.setAttribute('id',id + 'centercenter');
                                cr = document.createElement('div');
				cr.setAttribute('id',id + 'centerright');

                                b = document.createElement('div');
				b.setAttribute('id',id + 'bottom');
                                bl = document.createElement('div');
				bl.setAttribute('id',id + 'bottomleft');
                                bc = document.createElement('div');
				bc.setAttribute('id',id + 'bottomcenter');
                                br = document.createElement('div');
				br.setAttribute('id',id + 'bottomright');

				t.appendChild(tl);
				t.appendChild(cl);
				t.appendChild(bl);

				c.appendChild(tc);
				c.appendChild(cc);
				c.appendChild(bc);

                                b.appendChild(tr);
				b.appendChild(cr);
				b.appendChild(br);

				ct.appendChild(t);
				ct.appendChild(c);
				ct.appendChild(b);

				document.body.appendChild(ct);
				ct.style.opacity = 0;
				ct.style.filter = 'alpha(opacity=0)';
				document.onmousemove = this.pos;
			}
                        shift = z;
			ct.style.display = 'block';
			c.innerHTML = v;
			ct.style.width = w ? w + 'px' : 'auto';
			if(!w && ie){
				t.style.display = 'none';
				b.style.display = 'none';
				ct.style.width = ct.offsetWidth;
				t.style.display = 'block';
				b.style.display = 'block';
			}
                        crtWidth = w;
			if(ct.offsetWidth > maxw) {
                            ct.style.width = maxw + 'px';
                            crtWidth = maxw;
                        }
			h = parseInt(ct.offsetHeight) + top;
			clearInterval(ct.timer);
			ct.timer = setInterval(function(){cooltip.fade(1)},timer);
		},
		pos:function(){
                        var myWidth = ie ? document.documentElement.clientWidth : window.innerWidth;
			ct.style.top  = (crtHeight - ct.style.height)     + 'px';
			ct.style.left = (myWidth - crtWidth + shift - 10) + 'px';
		},
		fade:function(d){
			var a = alpha;
			if((a != endalpha && d == 1) || (a != 0 && d == -1)) {
				var i = speed;
				if(endalpha - a < speed && d == 1) {
					i = endalpha - a;
				} else if(alpha < speed && d == -1) {
					i = a;
				}
				alpha = a + (i * d);
				ct.style.opacity = alpha * .01;
				ct.style.filter = 'alpha(opacity=' + alpha + ')';
			} else {
				clearInterval(ct.timer);
				if(d == -1){ct.style.display = 'none'}
			}
		},
		hide:function(){
			clearInterval(ct.timer);
			ct.timer = setInterval(function(){cooltip.fade(-1)},timer);
		}
	};
}();

function getAboutContent() {
    var con = "" +
    '<table width="100%">'+
    '  <tr>' +
    '    <td>' +
    '      <b>About</b>' +
    '    </td>' +
    '    <td align="right">' +
    '      &nbsp;<br />' +
    '    </td>' +
    '  </tr>' +
    '  <tr>' +
    '    <td colspan="2">' +
    '      <hr />' +
    '    </td>' +
    '  </tr>' +
    '  <tr>' +
    '    <td colspan="2">' +
    'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus vitae ante vel tortor imperdiet hendrerit. '+
    'Maecenas blandit lorem non leo placerat sed pharetra metus facilisis. Donec et mauris lacus. Nullam ipsum urna,'+
    ' feugiat non iaculis non, imperdiet nec ante. Fusce feugiat lorem vel augue fermentum laoreet. Mauris aliquam '+
    'feugiat eros, eu viverra metus mattis ut. Quisque a pharetra eros. In hac habitasse platea dictumst. Maecenas '+
    'sed convallis odio. Nam in ligula nunc. Proin ullamcorper orci nec felis bibendum pretium. Sed iaculis, urna '+
    'dapibus vestibulum vulputate, libero elit porta diam, ut tempor ante libero at ipsum. Integer pharetra eros '+
    'convallis ipsum venenatis id tincidunt enim varius. Proin congue nisl ut orci porta dapibus. Etiam adipiscing '+
    'feugiat metus sed malesuada. Nam in risus purus, quis ultrices felis. Ut dictum metus eu lorem lobortis vulputate.'+
    ' Vestibulum vel lorem id est mollis aliquam.' +
    '    </td>' +
    '  </tr>' +
    '  <tr>' +
    '  <tr>' +
    '    <td>' +
    '      &nbsp;' +
    '    </td>' +
    '    <td align="right">' +
    '      &nbsp;' +
    '    </td>' +
    '  </tr>' +
    '  <tr>' +
    '</table>';
    return con;
}

function getHelpContent() {
    var con = "" +
    '<table width="100%">'+
    '  <tr>' +
    '    <td>' +
    '      <b>Help</b>' +
    '    </td>' +
    '    <td align="right">' +
    '      &nbsp;<br />' +
    '    </td>' +
    '  </tr>' +
    '  <tr>' +
    '    <td colspan="2">' +
    '      <hr />' +
    '    </td>' +
    '  </tr>' +
    '  <tr>' +
    '    <td colspan="2">' +
    'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus vitae ante vel tortor imperdiet hendrerit. '+
    'Maecenas blandit lorem non leo placerat sed pharetra metus facilisis. Donec et mauris lacus. Nullam ipsum urna,'+
    ' feugiat non iaculis non, imperdiet nec ante. Fusce feugiat lorem vel augue fermentum laoreet. Mauris aliquam '+
    'feugiat eros, eu viverra metus mattis ut. Quisque a pharetra eros. In hac habitasse platea dictumst. Maecenas '+
    'sed convallis odio. Nam in ligula nunc. Proin ullamcorper orci nec felis bibendum pretium. Sed iaculis, urna '+
    'dapibus vestibulum vulputate, libero elit porta diam, ut tempor ante libero at ipsum. Integer pharetra eros '+
    'convallis ipsum venenatis id tincidunt enim varius. Proin congue nisl ut orci porta dapibus. Etiam adipiscing '+
    'feugiat metus sed malesuada. Nam in risus purus, quis ultrices felis. Ut dictum metus eu lorem lobortis vulputate.'+
    ' Vestibulum vel lorem id est mollis aliquam.' +
    '    </td>' +
    '  </tr>' +
    '  <tr>' +
    '  <tr>' +
    '    <td>' +
    '      &nbsp;' +
    '    </td>' +
    '    <td align="right">' +
    '      &nbsp;' +
    '    </td>' +
    '  </tr>' +
    '  <tr>' +
    '</table>';
    return con;
}

function getInputContent() {
    var con = "" +
    '<table width="100%">'+
    '  <tr>' +
    '    <td>' +
    '      <b>Input</b>' +
    '    </td>' +
    '    <td align="right">' +
    '      &nbsp;<br />' +
    '    </td>' +
    '  </tr>' +
    '  <tr>' +
    '    <td colspan="2">' +
    '      <hr />' +
    '    </td>' +
    '  </tr>' +
    '  <tr>' +
    '    <td colspan="2">' +
    'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus vitae ante vel tortor imperdiet hendrerit. '+
    'Maecenas blandit lorem non leo placerat sed pharetra metus facilisis. Donec et mauris lacus. Nullam ipsum urna,'+
    ' feugiat non iaculis non, imperdiet nec ante. Fusce feugiat lorem vel augue fermentum laoreet. Mauris aliquam '+
    'feugiat eros, eu viverra metus mattis ut. Quisque a pharetra eros. In hac habitasse platea dictumst. Maecenas '+
    'sed convallis odio. Nam in ligula nunc. Proin ullamcorper orci nec felis bibendum pretium. Sed iaculis, urna '+
    'dapibus vestibulum vulputate, libero elit porta diam, ut tempor ante libero at ipsum. Integer pharetra eros '+
    'convallis ipsum venenatis id tincidunt enim varius. Proin congue nisl ut orci porta dapibus. Etiam adipiscing '+
    'feugiat metus sed malesuada. Nam in risus purus, quis ultrices felis. Ut dictum metus eu lorem lobortis vulputate.'+
    ' Vestibulum vel lorem id est mollis aliquam.' +
    '    </td>' +
    '  </tr>' +
    '  <tr>' +
    '  <tr>' +
    '    <td>' +
    '      &nbsp;' +
    '    </td>' +
    '    <td align="right">' +
    '      &nbsp;' +
    '    </td>' +
    '  </tr>' +
    '  <tr>' +
    '</table>';
    return con;
}
