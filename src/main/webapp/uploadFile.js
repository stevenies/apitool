/**
 * Opens a jQuery UI modal dialog, loads a form from an external HTML URL,
 * and on submit reloads the dialog with the returned HTML.
 *
 * Requires:
 * - jQuery
 * - jQuery UI (dialog)
 *
 * Example:
 * openAjaxFormDialog({
 *   loadUrl: "/account/editForm",      // returns HTML form
 *   title: "Edit Account",
 *   width: 900
 * });
 */
function openAjaxFormDialog(options) {
  var settings = $.extend(
    {
      loadUrl: null,               // required: URL returning initial HTML form
      title: "Form",
      width: 900,
      height: "auto",
      method: "GET",               // initial load method
      dialogId: "ajaxFormDialog",
      closeOnEsc: true
    },
    options || {}
  );

  if (!settings.loadUrl) {
    throw new Error("openAjaxFormDialog: loadUrl is required.");
  }

  var $dialog = $("#" + settings.dialogId);

  // Create dialog container if missing
  if ($dialog.length === 0) {
    $dialog = $('<div id="' + settings.dialogId + '"></div>').appendTo("body");
  }

  // Centralized function to load/replace dialog HTML
  function loadDialogHtml(url, ajaxOptions) {
    return $.ajax(
      $.extend(
        {
          url: url,
          method: "GET",
          dataType: "html"
        },
        ajaxOptions || {}
      )
    )
      .done(function (html) {
        $dialog.html(html);
      })
      .fail(function (xhr) {
        var msg = xhr.responseText || "Unable to load content.";
        $dialog.html('<div class="error">' + msg + "</div>");
      });
  }

  // Open modal first with loading indicator
  $dialog
    .html("<p>Loading...</p>")
    .dialog({
      modal: true,
      title: settings.title,
      width: settings.width,
      height: settings.height,
      closeOnEscape: settings.closeOnEsc,
      // Optional cleanup; remove this if you want to keep the element cached
      close: function () {
        $dialog.html("");
      }
    });

  // Initial form load from external HTML
  loadDialogHtml(settings.loadUrl, { method: settings.method });

  // Delegate submit so it still works after dialog HTML is replaced
  $dialog.off("submit.ajaxForm").on("submit.ajaxForm", "form", function (e) {
    e.preventDefault();

    var $form = $(this);
    var action = $form.attr("action") || settings.loadUrl;
    var formMethod = ($form.attr("method") || "POST").toUpperCase();

    // Serialize normal form fields; if file upload is needed, use FormData branch
    var hasFileInput = $form.find('input[type="file"]').length > 0;

    if (hasFileInput) {
      var formData = new FormData($form[0]);
      loadDialogHtml(action, {
        method: formMethod,
        data: formData,
        processData: false,
        contentType: false
      });
    } else {
      loadDialogHtml(action, {
        method: formMethod,
        data: $form.serialize()
      });
    }
  });

  return $dialog;
}