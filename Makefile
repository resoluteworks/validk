export OP_ACCOUNT := my.1password.com

include .env
export

include gradle.properties

env:
	rm -f .env
	op read "op://Development/Resolute Works - Open Source/validk.env.local" > .env

test:
	./gradlew clean test
	./gradlew coverallsJacoco

publish: test
	./gradlew publish -PpublishToMavenCentral=true

publish-local: test
	./gradlew publish -PpublishToMavenCentral=false

release: publish
	@echo $(validkVersion)
	git tag "v$(validkVersion)" -m "Release v$(validkVersion)"
	git push --tags --force
	@echo Finished building version $(validkVersion)
