/**
 * Rescriptのコンパイル後のデータを扱うヘルパー
 */

/**
 * @param {string} message
 */
export function Err(message) {
  return   {
    TAG: 'Error',
    _0: message
  }
}

/**
 * @template T
 * @param {T} message
 */
export function Ok(v) {
  return {
    TAG: 'Ok',
    _0: v
  }
}
