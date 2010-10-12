<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Score reviews</title>
    <link type="text/css" href="css/diffreviews.css" media="screen" rel="stylesheet"/>
    <link type="text/css" href="css/cooltip.css" media="screen" rel="stylesheet"/>
    <script type="text/javascript" src="js/diffreviews.js"></script>
    <script type="text/javascript" src="js/cooltip.js"></script>
    <script type="text/javascript" src="js/prototype.js"></script>
    <script type="text/javascript" src="js/scriptaculous.js"></script>
    <script type="text/javascript" src="js/controls.js"></script>
    <script type="text/javascript" src="js/effects.js"></script>
  </head>
  <body onload="init();">
    <table width="100%" align="center" border="0" cellspacing="0">
      <tr>
        <td align="center">&nbsp;<img src="img/tvd.png" alt="logo"/></td>
        <td colspan="2" align="right" class="title">&nbsp;Five Star Review Scoring&nbsp;</td>
      </tr>
      <tr>
        <td class="line">&nbsp;</td>
        <td align="right" colspan="2" class="line">
            <a style="cursor:pointer" onClick="coolTip(this, 'about', getAboutContent(), 400, 200, 20, '#ffefaf', 'default');">about</a>
            &nbsp;|&nbsp;
            <a style="cursor:pointer" onClick="coolTip(this, 'help', getHelpContent(), 300, 300, 20, '#22e4ff', 'orange');">help</a>
            &nbsp;
        </td>
      </tr>
      <tr>
        <td colspan="3"><div id="message" class="message"></div></td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td align="right">&nbsp;</td>
        <td align="center">
          <input type="text" size="42" id="stars"/>&nbsp;
          <input type="button" value="Get Score" id="btnScore" name="btnScore" onClick="getScore()"/>
        </td>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td colspan="3" align="center"><img src="img/background.png" alt=" " /></td>
      </tr>
      <tr>
        <td align="center"><img src="img/summary_top.png" alt=" " style="cursor:pointer" onclick="Effect.toggle('summary', 'slide', { duration: 0.3 });return false;"/></td>
        <td align="center"><img src="img/score_top.png" alt=" " style="cursor:pointer" onclick="Effect.toggle('score_table', 'slide', { duration: 0.3 });return false;"/></td>
        <td align="center"><img src="img/setting_top.png" id="settop" alt=" " style="cursor:pointer" onclick="toggleSettingHistory();"/></td>
      </tr>
      <tr>
        <td align="center" valign="top" style="width:160px; height:190px;">
          <div id="summary" style="display:none; width:160px; height:190px; background-image:url('img/summary_table.png'); text-align:center;">
            <div>
                <table border="0" width="100%" cellspacing="6">
                <tr>
                  <td align="right" width="57%"><img src="img/5star.png" alt="5 stars" /></td>
                  <td><div id="5stars" class="star_no"></div></td>
                </tr>
                <tr>
                  <td align="right"><img src="img/4star.png" alt="4 stars" /></td>
                  <td><div id="4stars" class="star_no"></div></td>
                </tr>
                <tr>
                  <td align="right"><img src="img/3star.png" alt="3 stars" /></td>
                  <td><div id="3stars" class="star_no"></div></td>
                </tr>
                <tr>
                  <td align="right"><img src="img/2star.png" alt="2 stars" /></td>
                  <td><div id="2stars" class="star_no"></div></td>
                </tr>
                <tr>
                  <td align="right"><img src="img/1star.png" alt="1 star" /></td>
                  <td><div id="1stars" class="star_no"></div></td>
                </tr>
              </table>
            </div>
          </div>
        </td>
        <td align="center" valign="top" style="width:480px; height:190px;">
          <div id="score_table" style="display:none; width:480px; height:186px; background-image:url('img/score_table.png'); text-align:center;">
            <div>
              <table border="0" width="100%">
                <tr>
                  <td class="score" id="score"></td>
                </tr>
              </table>
            </div>
          </div>
        </td>
        <td align="center" valign="top" style="width:280px; height:190px;">
          <div id="settings" style="display:none; width:280px; height:190px;  background-image:url('img/setting_table.png'); text-align:center;">
            <div>
              <table width="100%" border="0" cellspacing="0">
                <tr>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                </tr>
                <tr>
                  <td align="right" class="menu">&nbsp;Method:&nbsp;</td>
                  <td align="left">
                    <select id="method">
                      <option selected="true">Default</option>
                      <option>Method 2</option>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td align="right" class="menu">&nbsp;Confidence:&nbsp;</td>
                  <td align="left">
                    <select id="confidence">
                      <option>1</option>
                      <option>2</option>
                      <option>5</option>
                      <option selected="true">10</option>
                      <option>50</option>
                      <option>100</option>
                      <option>20</option>
                      <option>500</option>
                      <option>1000</option>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td align="right" class="menu">&nbsp;Decimals:&nbsp;</td>
                  <td align="left">
                    <select id="decimals" onchange="formatScore();">
                      <option>1</option>
                      <option selected="true">2</option>
                      <option>3</option>
                      <option>4</option>
                      <option>5</option>
                    </select>
                  </td>
                </tr>
              </table>
            </div>
          </div>
          <div id="history" style="display:none; width:280px; height:190px;  background-image:url('img/setting_table.png'); text-align:center;">
            <div>
                <table id="past_score_table" width="100%" border="0" cellspacing="4" class="history">
                <tr><td align="right" width="16%">&nbsp;</td>
                    <td align="right" width="16%">&nbsp;</td>
                    <td align="right" width="16%">&nbsp;</td>
                    <td align="right" width="16%">&nbsp;</td>
                    <td align="right" width="16%">&nbsp;</td>
                    <td align="center" width="4%">&nbsp;</td>
                    <td align="right" width="16%">&nbsp;</td>
                </tr>
                <tr><td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="center">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                </tr>
                <tr><td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="center">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                </tr>
                <tr><td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="center">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                </tr>
                <tr><td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="center">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                </tr>
                <tr><td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="center">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                </tr>
                <tr><td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="center">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                </tr>
                <tr><td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                    <td align="center">&nbsp;</td>
                    <td align="right">&nbsp;</td>
                </tr>
              </table>
            </div>
          </div>
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
      </tr>
    </table>
  </body>
</html>
