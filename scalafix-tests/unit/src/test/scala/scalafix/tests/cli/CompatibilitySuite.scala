package scalafix.tests.cli

import org.scalatest.funsuite.AnyFunSuite
import scalafix.internal.util.Compatibility
import scalafix.internal.util.Compatibility._

class CompatibilitySuite extends AnyFunSuite {

  // to avoid struggles when testing nightlies
  test("EarlySemver unknown if run or build is a snapshot") {
    assert(
      Compatibility.earlySemver(
        builtAgainst = "0.9.34+52-a83785c4-SNAPSHOT",
        runWith = "1.2.3"
      ) == Unknown
    )
    assert(
      Compatibility.earlySemver(
        builtAgainst = "0.9.34",
        runWith = "1.2.3+1-bfe5ccd4-SNAPSHOT"
      ) == Unknown
    )
  }

  // backward compatibility within X.*.*, 0.Y.*, ...
  test(
    "EarlySemver compatible if run is equal or greater by minor (or patch in 0.)"
  ) {
    assert(
      Compatibility.earlySemver(
        builtAgainst = "1.3.27",
        runWith = "1.3.28"
      ) == Compatible
    )
    assert(
      Compatibility.earlySemver(
        builtAgainst = "1.10.20",
        runWith = "1.12.0"
      ) == Compatible
    )
    assert(
      Compatibility.earlySemver(
        builtAgainst = "0.6.12",
        runWith = "0.6.12"
      ) == Compatible
    )
    assert(
      Compatibility.earlySemver(
        builtAgainst = "0.9.0",
        runWith = "0.9.20"
      ) == Compatible
    )
  }

  // no forward compatibility: build might reference classes unknown to run
  test("EarlySemver incompatible if run is lower by minor (or patch in 0.)") {
    assert(
      Compatibility.earlySemver(
        builtAgainst = "0.10.8",
        runWith = "0.9.16"
      ) == Incompatible
    )
    assert(
      Compatibility.earlySemver(
        builtAgainst = "0.10.17",
        runWith = "0.10.4"
      ) == Incompatible
    )
    assert(
      Compatibility.earlySemver(
        builtAgainst = "2.0.0",
        runWith = "1.1.1"
      ) == Incompatible
    )
    assert(
      Compatibility.earlySemver(
        builtAgainst = "1.4.7",
        runWith = "1.2.8"
      ) == Incompatible
    )
  }

  // might be false positive/negative tree matches or link failures
  test("EarlySemver unknown if run is greater by major (or minor in 0.)") {
    assert(
      Compatibility.earlySemver(
        builtAgainst = "1.0.41",
        runWith = "2.0.0"
      ) == Unknown
    )
    assert(
      Compatibility.earlySemver(
        builtAgainst = "0.9.38",
        runWith = "0.10.2"
      ) == Unknown
    )
    assert(
      Compatibility.earlySemver(
        builtAgainst = "0.9.38",
        runWith = "1.0.0"
      ) == Unknown
    )
  }
}
