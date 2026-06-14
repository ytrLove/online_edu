/**
 * 日期格式化函数
 * @param {Date} date 日期对象
 * @param {String} fmt 格式化字符串
 * @returns {String} 格式化后的日期字符串
 */
export function formatDate(date, fmt) {
  if (!date) return ''
  if (typeof date === 'string') {
    date = new Date(date)
  }
  
  const o = {
    'M+': date.getMonth() + 1, // 月份
    'D+': date.getDate(), // 日
    'H+': date.getHours(), // 小时
    'm+': date.getMinutes(), // 分
    's+': date.getSeconds(), // 秒
    'q+': Math.floor((date.getMonth() + 3) / 3), // 季度
    S: date.getMilliseconds() // 毫秒
  }

  if (/(Y+)/.test(fmt)) {
    fmt = fmt.replace(
      RegExp.$1,
      (date.getFullYear() + '').substr(4 - RegExp.$1.length)
    )
  }

  for (const k in o) {
    if (new RegExp('(' + k + ')').test(fmt)) {
      fmt = fmt.replace(
        RegExp.$1,
        RegExp.$1.length === 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length)
      )
    }
  }
  return fmt
}

/**
 * 获取相对时间描述
 * @param {Date|String} date 日期对象或日期字符串
 * @returns {String} 相对时间描述
 */
export function relativeTime(date) {
  if (!date) return ''
  
  if (typeof date === 'string') {
    date = new Date(date)
  }
  
  const now = new Date()
  const diff = now - date
  
  // 转换为秒
  const diffSeconds = Math.floor(diff / 1000)
  
  if (diffSeconds < 60) {
    return '刚刚'
  }
  
  // 转换为分钟
  const diffMinutes = Math.floor(diffSeconds / 60)
  if (diffMinutes < 60) {
    return `${diffMinutes}分钟前`
  }
  
  // 转换为小时
  const diffHours = Math.floor(diffMinutes / 60)
  if (diffHours < 24) {
    return `${diffHours}小时前`
  }
  
  // 转换为天
  const diffDays = Math.floor(diffHours / 24)
  if (diffDays < 30) {
    return `${diffDays}天前`
  }
  
  // 转换为月
  const diffMonths = Math.floor(diffDays / 30)
  if (diffMonths < 12) {
    return `${diffMonths}个月前`
  }
  
  // 转换为年
  const diffYears = Math.floor(diffMonths / 12)
  return `${diffYears}年前`
}

/**
 * 判断日期是否在指定天数内
 * @param {Date|String} date 日期对象或日期字符串
 * @param {Number} days 天数
 * @returns {Boolean} 是否在指定天数内
 */
export function isWithinDays(date, days) {
  if (!date) return false
  
  if (typeof date === 'string') {
    date = new Date(date)
  }
  
  const now = new Date()
  const diff = now - date
  const diffDays = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  return diffDays <= days
} 