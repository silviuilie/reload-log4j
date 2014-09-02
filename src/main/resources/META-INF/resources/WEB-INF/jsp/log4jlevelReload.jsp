<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <script type="text/javascript" src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
    <script src="http://code.jquery.com/ui/1.10.4/jquery-ui.min.js"></script>
    <link href="http://code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css" rel="stylesheet">
    <script type="text/javascript">
        /**   url configuration.  */
        var _tools = {
            findURL: '<c:url value="/log4jFindClass.log4j"/>',
            changeURL: '<c:url value="/log4jReload.log4j"/>'
        };

        function _notify(container, message) {
            $('#log').before(
                    $(container).clone(true)
                            .fadeIn()
                            .find('p').html(message)
                            .parent().delay(2000).fadeOut(500, function () {
                                $(this).remove();
                            })
            );
            $('#allChangesLog').append('<p>' + message + '</p>');

        }

        /**
         * creates the widgets, sets various stuff to default states.
         **/
        function init() {

            $('#allChangesLog').dialog({ modal: true, autoOpen: false, height: 200, width: 800});

            $('#loading, #success, #error').hide();

            $(document).bind("ajaxSend",function () {
                $('#loading').fadeIn();
            }).bind("ajaxComplete", function (evt, xhr) {
                        _notify('#' + xhr.responseJSON.type.toLowerCase(), xhr.responseJSON.message);
                        $('#loading').hide();
                    });

            /**
             * init class name search autocomplete.
             */
            $("#classNameAutocomplete").autocomplete({
                source: function (request, response) {
                    $.ajax({
                        method: "POST",
                        url: _tools.findURL,
                        dataType: "json",
                        data: { name: request.term },
                        success: function (data) {
                            response($.map(data, function (item) {
                                return { label: item, id: item };
                            }));
                        }
                    });
                },
                minLength: 2
            });

            /**
             * define event handlers.
             */
            $(function () {
                $(".ui-button")
                        .button()
                        .click(function (event) {
                            console.log('event.currentTarget.id = ', event.currentTarget.id)
                            if ($('#classNameAutocomplete').val() == "") {
                                showError("specify a class name / fragment");
                                $('#classNameAutocomplete').focus();
                            }
                            if (event.currentTarget.id == 'allChanges') {
                                $('#allChangesLog').dialog("open");
                            } else if (event.currentTarget.id) {
                                $('#loading, #error').hide();
                                changePriority($('#classNameAutocomplete').val(), $(this).text().replace("original", "").trim());
                            }
                            event.preventDefault();
                        });
            });
        }

        /**
         * triggers change class priority request.
         *
         * @param className class name fragment.
         * @param priority new priority / reset to default.
         */
        function changePriority(className, priority) {
            $.ajax({
                method: "POST",
                url: _tools.changeURL,
                dataType: "json",
                data: { target: className, priority: priority}
            });
        }

        $(document).ready(function () {
            init();
        });
    </script>
<head>
<body style="font-family: Courier;height: 70%;">
<div style="min-height: 100%;">
    <div style="overflow:auto;padding-bottom: 120px;">
        <div class="ui-widget">
            <div class="ui-state-highlight ui-corner-all">
                <div style="margin-left:10px;">
                    <p>
                    <h5>type class name / fully qualified name.</h5>
                    <h6>you can reload log4j priority for classes in the
                        <span style="font-style:italic">configured</span> package only.
                    </h6>
					<span>
						<input id="classNameAutocomplete" title="type &quot;a&quot;"
                               style="border: none;height:36px;width:99%;"/>
					</span>
                    </p>
                    <p>
					<span>
						<button id="debugButton"
                                class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"
                                role="button" aria-disabled="false">DEBUG
                        </button>
					</span>
					<span>
						<button id="infoButton"
                                class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"
                                role="button" aria-disabled="false">
                            INFO
                        </button>
					</span>
					<span>
						<button id="errorButton"
                                class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"
                                role="button" aria-disabled="false">
                            ERROR
                        </button>
					</span>
					<span>
						<button id="restore"
                                class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"
                                role="button" aria-disabled="false">
                            restore original
                        </button>
					</span>
					<span style="float: right;padding-right: 10px;">
						<button id="allChanges"
                                class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"
                                role="button" aria-disabled="false">
                            all changes
                        </button>
					</span>
                    </p>
                </div>
            </div>
        </div>
        <div id="log"><!-- current action --></div>
        <div id="allChangesLog" style="font-size: 9px;" title="all changes"><!-- all logs --></div>
        <div id="loading">
            <div class="ui-state-highlight ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                <p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
                    loading ..
                </p>
            </div>
        </div>
        <div id="error">
            <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                <p><span id="errorDetails" class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
                    error
                </p>
            </div>
        </div>
        <div id="success">
            <div class="ui-state-highlight ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                <p><span id="successDetails" class="ui-icon ui-icon-info"
                         style="float: left; margin-right: .3em;"></span>
                    success
                </p>
            </div>
        </div>
    </div>
</div>
<div class="ui-state-highlight ui-corner-all"
     style="font-size:11px;margin-top: -120px;height: 120px;margin-top: 40px; padding: 20 .7em;position: relative;clear:both;">
    <div class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></div>
    <p>

    <div>
        <div>log4j reload utility parameters :</div>
        <dl>
            <dt>base package name</dt>
            <dd><strong> ${requestScope['log4j-base-packageName']} </strong></dd>
            <dt>authorization class</dt>
            <dd>
                <strong>${requestScope['log4j-authorization-class']}
                    <c:if test="${empty requestScope['log4j-authorization-class']}">not configured, allow all</c:if></strong>
            </dd>
            <dt>jsp location</dt>
            <dd>
                <strong>${requestScope['log4j-jsp-location']}
                    <c:if test="${empty requestScope['log4j-jsp-location']}">not configured</c:if></strong>
            </dd>
        </dl>
        <div>
            <br/>&nbsp;<a href="#">fork</a>
        </div>
    </div>
    </p>
</div>
</body>
</html>