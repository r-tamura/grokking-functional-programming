import assert from "node:assert"
import { describe, it } from "node:test"
import { addOrResign, extractAnyYear, extractSingleYearIfNameExists, extractSingleYearOrYearEnd } from "./ch06_TvShows.res.js"


describe("6.29: 関数型のエラー処理", () => {
  describe("1シーズンの年を抽出し、失敗した場合は終了念を週出する", () => {
    it("開始年だけがあるとき、年を抽出しない", () => {
      assert.strictEqual(extractSingleYearOrYearEnd("A (2019-)"), undefined)
    })

    it("1シーズンだけのとき、その年を抽出する", () => {
      assert.strictEqual(extractSingleYearOrYearEnd("B (2002)"), 2002)
    })
  })

  describe("開始年 -> 終了年 -> 1シーズンの年の順で年を抽出する", () => {
    it("開始年と終了年があるとき、開始年を抽出する", () => {
      assert.strictEqual(extractAnyYear("C (2010-2011)"), 2010)
    })

    it("終了年だけがあるとき、終了年を抽出する", () => {
      assert.strictEqual(extractAnyYear("D (-2013)"), 2013)
    })

    it("1シーズンの年があるとき、1シーズンの年を抽出する", () => {
      assert.strictEqual(extractAnyYear("E (2015)"), 2015)
    })
  })

  describe("タイトルがあるとき、1シーズンの年を抽出する", () => {
    it("タイトルがあるとき、1シーズンの年を抽出する", () => {
      assert.strictEqual(extractSingleYearIfNameExists("F (2019)"), 2019)
    })

    it("タイトルがないとき、undefinedを返す", () => {
      assert.strictEqual(extractSingleYearIfNameExists("(2019)"), undefined)
    })
  })

  describe("タイトルがあるとき、開始年 -> 終了年 -> 1シーズンの年の順で年を抽出する", () => {
    it("タイトルがあるとき、開始年を抽出する", () => {
      assert.strictEqual(extractAnyYear("G (2010-2011)"), 2010)
    })

    it("タイトルがあるとき、終了年を抽出する", () => {
      assert.strictEqual(extractAnyYear("H (-2013)"), 2013)
    })

    it("タイトルがあるとき、1シーズンの年を抽出する", () => {
      assert.strictEqual(extractAnyYear("I (2015)"), 2015)
    })
  })
})

describe("6.36: エラー処理の戦略", () => {
  it("TVショーリストが空で新しいTVショーがあるとき、新しいTVショーが追加される", () => {
    assert.deepStrictEqual(
      addOrResign([], {title: "Chernobyl", start: 2019, end: 2019}),
      [{title: "Chernobyl", start: 2019, end: 2019}]
    )
  })
  it("TVショーリストにTVショーがあり、新しいTVショーが渡されたとき、新しいTVショーが追加される", () => {
    assert.deepStrictEqual(
      addOrResign([{title: "Chernobyl", start: 2019, end: 2019}], {title: "The Wire", start: 2002, end: 2008}),
      [{title: "Chernobyl", start: 2019, end: 2019}, {title: "The Wire", start: 2002, end: 2008}]
    )
  })
  it("TVショーリストがあり、新しいTVショーがNoneのとき、Noneが返る", () => {
    assert.strictEqual(addOrResign([], undefined), undefined)
  })
  it("TVショーリストがないとき、Noneが返える", () => {
    assert.strictEqual(addOrResign(undefined, {title: "Chernobyl", start: 2019, end: 2019}), undefined)
  })
  it("TVショーリストも新しいTVショーもないとき、Noneが返る", () => {
    assert.strictEqual(addOrResign(undefined, undefined), undefined)
  })
})