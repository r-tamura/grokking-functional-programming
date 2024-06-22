import { strictEqual } from "node:assert";
import { describe, it } from "node:test";
import { activeSearch } from "./ch07_Artist.res.js";

const StillActive = (since) => ({
  TAG: "StillActive",
  _0: since,
});

const ActiveBetween = (start, end) => ({
  TAG: "ActiveBetween",
  _0: start,
  _1: end,
})

describe("7.29 パターンマッチング", () => {
  it("アーティストが活動中のとき、活動開始年から現在までの活動年数を返す", () => {
    const artist = {
      name: "A",
      genre: "Pop",
      origin: "U.S.",
      yearsActive: StillActive(1981),
    }
    strictEqual(activeSearch(artist, 2022), 41)
  })

  it("アーティストが活動終了しているとき、活動期間の年数を返す", () => {
    const artist = {
      name: "Led Zeppelin",
      genre: "HardRock",
      origin: "U.K.",
      yearsActive: ActiveBetween(1968, 1980),
    };
    strictEqual(activeSearch(artist, 2022), 12)
  })
})