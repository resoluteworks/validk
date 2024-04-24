export OP_ACCOUNT := my.1password.com

include .env
export

include gradle.properties

env:
	rm -f .env
	op read "op://Development/resolute-works-open-source/validk.env.local" > .env

test:
	./gradlew clean test
	./gradlew coverallsJacoco

publish:
	./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository

publish-local:
	./gradlew publish -x initializeSonatypeStagingRepository -x publishMavenJavaPublicationToSonatypeRepository

release: test publish-local publish
	@echo $(validkVersion)
	git tag "v$(validkVersion)" -m "Release v$(validkVersion)"
	git push --tags --force
	@echo Finished building version $(validkVersion)
