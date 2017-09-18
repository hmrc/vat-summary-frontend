# vat summary frontend Contributor Guidelines

Hello! Thank you for taking the time to contribute to [vat-summary-frontend](https://github.com/hmrc/vat-summary-frontend).

## General Contributor Guidelines

Before you go any further, please read the general [MDTP Contributor Guidelines](https://github.com/hmrc/mdtp-contributor-guidelines/blob/master/CONTRIBUTING.md).
It would be helpful if you were to talk to the team before making a pull request to stop any conflicts that may occur.

## vat summary frontend Guidelines

First run `sbt test`. If those tests do not work, you are not ready to raise a PR.

Then make sure the acceptance tests pass. This can be done by running the `run_integration_vat_summary.sh` script within
the `vat-view-and-change-acceptance-tests` repository. You will need to start `VAT_VC_ALL` in [Service Manager](https://github.com/hmrc/service-manager) before running the script.

If those pass you need to get someone off the [#team-vat-vc](https://hmrcdigital.slack.com/messages/team-vat-vc/) to merge the pull request.
