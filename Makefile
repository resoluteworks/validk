include gradle.properties

test:
	./gradlew clean test
	COVERALLS_REPO_TOKEN=$$COVERALLS_VALIDK ./gradlew coverallsJacoco

publish:
	./gradlew publishAggregationToCentralPortal

publish-local:
	./gradlew publish

release: test publish-local publish
	@echo $(validkVersion)
	git tag "v$(validkVersion)" -m "Release v$(validkVersion)"
	git push --tags --force
	@echo Finished building version $(validkVersion)
