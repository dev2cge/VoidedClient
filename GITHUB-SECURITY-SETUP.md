# GitHub repository security setup

After publishing the VoidedClient source repository, enable these repository settings. Workflow files cannot enable account-level security controls themselves.

## Code security

- Enable the dependency graph.
- Enable Dependabot alerts and security updates.
- Enable secret scanning and push protection.
- Enable private vulnerability reporting/security advisories.
- Keep GitHub Actions restricted to required actions and reviewed workflows.

## Branch protection

Protect `main` and require:

- a pull request before merging;
- the build-and-verify check;
- dependency review;
- at least one approving review;
- conversation resolution;
- no force pushes or branch deletion.

## Release protection

- Restrict creation of `client-v*` tags to maintainers.
- Use the release workflow artifacts without rebuilding locally.
- Do not allow an existing release JAR to be overwritten.
- Publish the checksum and provenance links beside every download.

The included CI performs a focused runtime-source audit and high-confidence secret scan. GitHub secret scanning and push protection add full-history and pre-push coverage.
