/**
 * Created by bazzoni on 03/04/2015.
 */
var editMode;
var dashboardConfig;
/**
 * Available widgets
 */
var WIDGETS = {
    sites: {
        template: "/template/sites",
        data: "/tunnel/sites",
        username: 'thebaz',
        password: 'q1w2e3r4',
        loadFunction: function (widget, element) {
            var request = widget.data + "?username=" + widget.username + "&password=" + widget.password;
            var response = $.getJSON(request);
            response.done(function (data) {
                $.get(widget.template, function (template) {
                    var t = Handlebars.compile(template);
                    element.append(t(data));
                    //element.find("#site-widget div[data-action='panel-buttons']").toggle();
                });
            });
        },
        refresh: function (widget) {
            var request = widget.data + "?username=" + widget.username + "&password=" + widget.password;
            var response = $.getJSON(request);
            response.done(function (data) {
                $.get("/template/sites_page", function (template) {
                    var t = Handlebars.compile(template);
                    $('#site-page').html(t(data));
                });
            });
        }
    }
};
/**
 * Available layouts
 */
var LAYOUTS = [
    {
        classNames: ["col-md-12", "col-md-6", "col-md-6", "col-md-12"]
    },
    {
        classNames: ["col-md-12", "col-md-4", "col-md-8", "col-md-12"]
    },
    {
        classNames: ["col-md-12", "col-md-8", "col-md-4", "col-md-12"]
    },
    {
        classNames: ["col-md-12", "col-md-5", "col-md-7", "col-md-12"]
    },
    {
        classNames: ["col-md-12", "col-md-7", "col-md-5", "col-md-12"]
    }
];
/**
 * Dashboard configuration objects
 */
var DEFAULT_CONFIG = {
    layout: 1,
    columns: [{
        widgets: []
    }, {
        widgets: []
    }, {
        widgets: ["sites"]
    }, {
        widgets: []
    }]
};

/**
 * Initial activities
 */
$(document).ready(function () {
    var $dashboard = $('#dashboard-commands');
    if (typeof(Storage) === "undefined") {
        console.log("Sorry! No Web Storage support..");
        $dashboard.find(".glyphicon-floppy-save").toggle(false);
    }
    dashboardConfig = JSON.parse(localStorage.getItem("dashboardConfig"));
    if (!dashboardConfig) {
        dashboardConfig = DEFAULT_CONFIG;
    }
    layoutPage(false);

    updateWidgetData();

    $dashboard.find('.glyphicon-edit').click(function (event) {
        event.preventDefault();
        editMode = !editMode;
        applyEditMode(editMode, true);
    });
    $dashboard.find('.glyphicon-cog').click(function (event) {
        event.preventDefault();
        $('#settingsModal').modal();
    });
    $dashboard.find('.glyphicon-floppy-save').click(function (event) {
        event.preventDefault();
        localStorage.setItem("dashboardConfig", JSON.stringify(dashboardConfig));
        editMode = false;
        applyEditMode(editMode, true);
        $('#saveModal').modal();
    });
    $("#closeSettings").click(function (/*event*/) {
        var layout = $("#settingsModal").find("input:checked").val();
        changeLayout(layout);
        layoutPage(true);
    });
});

function updateWidgetData() {
    for (var col = 0; col < dashboardConfig.columns.length; col++) {
        var column = dashboardConfig.columns[col];
        for (var w = 0; w < column.widgets.length; w++) {
            if (editMode) {
                break;
            }
            else {
                var widget = WIDGETS[column.widgets[w]];
                widget.refresh(widget);
            }
        }
    }

    setTimeout(updateWidgetData, updateFrequency);
}

/**
 * Function that renders layout
 *
 * @param force f
 */
function layoutPage(force) {
    var classNames = LAYOUTS[dashboardConfig.layout].classNames;
    for (var i = 0; i < classNames.length; i++) {
        var wrapper = $("#wrapper" + i);
        if (wrapper.length == 0) {
            wrapper = $('<div/>', {
                id: 'wrapper' + i
            });
            wrapper.appendTo("#dashboard");
            wrapper.data("wrapper", i);
        }
        else {
            wrapper.removeClass();
        }
        wrapper.addClass("wrapper");
        wrapper.addClass(classNames[i]);
    }
    for (var col = 0; col < dashboardConfig.columns.length; col++) {
        var column = dashboardConfig.columns[col];
        for (var w = 0; w < column.widgets.length; w++) {
            var widget = $("#" + column.widgets[w] + "-widget");
            if (widget.length == 0) {
                widget = WIDGETS[column.widgets[w]];
                widget.loadFunction(widget, $('#wrapper' + col));
            }
            else {
                widget.detach();
                widget.appendTo('#wrapper' + col);
            }
            /*if(column.widgets[w] === "map") {
             WIDGETS["map"].refreshMap();
             }*/
        }
    }
    editMode = false;
    applyEditMode(editMode, force);
}

/**
 * Applies edit mode
 *
 * @param mode edit mode
 * @param force force droppable and draggable destroy
 */
function applyEditMode(mode, force) {
    var $dashboard = $('#dashboard-commands');
    var $wrapper = $(".wrapper");
    $dashboard.find(".glyphicon-floppy-save").toggle(mode);
    $dashboard.find(".glyphicon-cog").toggle(mode);
    $("*[data-action='panel-buttons']").toggle(mode);

    if (mode) {
        $wrapper.addClass("editable-board");
        //$(".widget").css("max-width", "100px");
        $(".widget").draggable({
            revert: "invalid", // when not dropped, the item will revert back to its initial position
            cursor: "move"
        });
        $wrapper.droppable({
            accept: ".widget",
            activeClass: "activeWrapper",
            hoverClass: "hoverWrapper",
            drop: function (event, ui) {
                updateConfig(ui.draggable, $(this));
            }
        });
    }
    else {
        $wrapper.removeClass("editable-board");
        if (force) {
            $(".widget").draggable("destroy");
            $wrapper.droppable("destroy");
        }
    }

}

/**
 * Function that changes layout and dashboard configuration
 *
 * @param newLayout layout number shall be 0,1,2
 */
function changeLayout(newLayout) {
    var configuration = {
        layout: newLayout,
        columns: []
    };

    var allWidgets = [];
    for (var col = 0; col < dashboardConfig.columns.length; col++) {
        var column = dashboardConfig.columns[col];
        for (var w = 0; w < column.widgets.length; w++) {
            allWidgets.push(column.widgets[w]);
        }
    }

    configuration.columns.push({widgets: allWidgets});

    var classNames = LAYOUTS[newLayout].classNames;
    for (var i = 1; i < classNames.length; i++) {
        configuration.columns.push({widgets: []});
    }

    dashboardConfig = configuration;
    console.log(configuration);
}

/**
 * Updates dashboard configuration on DnD
 * @param draggable draggable item
 * @param droppable droppable item
 */
function updateConfig(draggable, droppable) {
    draggable.appendTo(droppable);
    draggable.css("position", "");
    draggable.css("top", "");
    draggable.css("left", "");
    // save moves;
    var widgetName = draggable.data("widget");
    var wrapperId = droppable.data("wrapper");
    for (var col = 0; col < dashboardConfig.columns.length; col++) {
        var obj = dashboardConfig.columns[col].widgets;
        var index = obj.indexOf(widgetName);
        if (index > -1) {
            dashboardConfig.columns[col].widgets = obj.splice(index, 1);
        }
    }
    dashboardConfig.columns[wrapperId].widgets.push(widgetName);
    /*if(widgetName === "map") {
     WIDGETS["map"].refreshMap();
     }*/
}