export OP_ACCOUNT := my.1password.com
include gradle.properties

ifneq (,$(wildcard .env))
	include .env
	export
endif

env:
	rm -f .env
	op read "op://Development/resolute-works-open-source/validk.env.local" > .env

test:
	./gradlew clean test
	./gradlew coverallsJacoco

publish:
	./gradlew publishAllPublicationsToCentralPortal

publish-local:
	./gradlew publish

release: test publish-local publish
	@echo $(validkVersion)
	git tag "v$(validkVersion)" -m "Release v$(validkVersion)"
	git push --tags --force
	@echo Finished building version $(validkVersion)
