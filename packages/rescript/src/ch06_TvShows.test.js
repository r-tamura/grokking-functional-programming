import assert from "node:assert"
import { describe, it } from "node:test"
import { addOrResign, extractAnyYear, extractSingleYearIfNameExists, extractSingleYearOrYearEnd } from "./ch06_TvShows.res.js"
import { Err, Ok } from "./test/helper.js"

describe("6.29: 関数型のエラー処理", () => {
  describe("1シーズンの年を抽出し、失敗した場合は終了念を週出する", () => {
    it("開始年だけがあるとき、年を抽出しない", () => {
      assert.deepStrictEqual(extractSingleYearOrYearEnd("A (2019-)"), Err("Can't extract end year from A (2019-)"))
    })

    it("1シーズンだけのとき、その年を抽出する", () => {
      assert.deepStrictEqual(extractSingleYearOrYearEnd("B (2002)"), Ok(2002))
    })
  })

  describe("開始年 -> 終了年 -> 1シーズンの年の順で年を抽出する", () => {
    it("開始年と終了年があるとき、開始年を抽出する", () => {
      assert.deepStrictEqual(extractAnyYear("C (2010-2011)"), Ok(2010))
    })

    it("終了年だけがあるとき、終了年を抽出する", () => {
      assert.deepStrictEqual(extractAnyYear("D (-2013)"), Ok(2013))
    })

    it("1シーズンの年があるとき、1シーズンの年を抽出する", () => {
      assert.deepStrictEqual(extractAnyYear("E (2015)"), Ok(2015))
    })
  })

  describe("タイトルがあるとき、1シーズンの年を抽出する", () => {
    it("タイトルがあるとき、1シーズンの年を抽出する", () => {
      assert.deepStrictEqual(extractSingleYearIfNameExists("F (2019)"), Ok(2019))
    })

    it("タイトルがないとき、undefinedを返す", () => {
      assert.deepStrictEqual(extractSingleYearIfNameExists("(2019)"), Err("Title is empty in (2019)"))
    })
  })

  describe("タイトルがあるとき、開始年 -> 終了年 -> 1シーズンの年の順で年を抽出する", () => {
    it("タイトルがあるとき、開始年を抽出する", () => {
      assert.deepStrictEqual(extractAnyYear("G (2010-2011)"), Ok(2010))
    })

    it("タイトルがあるとき、終了年を抽出する", () => {
      assert.deepStrictEqual(extractAnyYear("H (-2013)"), Ok(2013))
    })

    it("タイトルがあるとき、1シーズンの年を抽出する", () => {
      assert.deepStrictEqual(extractAnyYear("I (2015)"), Ok(2015))
    })
  })
})

describe("6.36: エラー処理の戦略", () => {
  it("TVショーリストが空で新しいTVショーがあるとき、新しいTVショーが追加される", () => {
    assert.deepStrictEqual(
      addOrResign(Ok([]), Ok({title: "Chernobyl", start: 2019, end: 2019})),
      Ok([{title: "Chernobyl", start: 2019, end: 2019}])
    )
  })
  it("TVショーリストにTVショーがあり、新しいTVショーが渡されたとき、新しいTVショーが追加される", () => {
    assert.deepStrictEqual(
      addOrResign(Ok([{title: "Chernobyl", start: 2019, end: 2019}]), Ok({title: "The Wire", start: 2002, end: 2008})),
      Ok([{title: "Chernobyl", start: 2019, end: 2019}, {title: "The Wire", start: 2002, end: 2008}])
    )
  })
  it("TVショーリストがあり、新しいTVショーがErrorのとき、Errorが返る", () => {
    assert.deepStrictEqual(addOrResign(Ok([]), Err("Error")), Err("Error"))
  })
  it("TVショーリストがないとき、Errorが返える", () => {
    assert.deepStrictEqual(addOrResign(Err("Error"), {title: "Chernobyl", start: 2019, end: 2019}), Err("Error"))
  })
  it("TVショーリストも新しいTVショーもないとき、Noneが返る", () => {
    assert.deepStrictEqual(addOrResign(Err("Error"), Err("")), Err("Error"))
  })
})