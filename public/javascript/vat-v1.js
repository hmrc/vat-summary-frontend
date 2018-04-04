$(document).ready(function() {
  //initialise <details> polyfill from frontend toolkit
  GOVUK.details.init();

  $('[data-metrics]').each(function () {
    var metrics = $(this).attr('data-metrics');
    var parts = metrics.split(':');
    ga('send', 'event', parts[0], parts[1], parts[2]);
  });
});