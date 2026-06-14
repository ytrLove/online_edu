import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

// 配置 dayjs
dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

/**
 * 格式化时间为相对时间
 * @param {string|Date} time 时间
 * @returns {string} 相对时间，如：3小时前、2天前
 */
export function formatTimeAgo(time) {
  if (!time) return '未知'
  return dayjs(time).fromNow()
}

/**
 * 格式化时间为指定格式
 * @param {string|Date} time 时间
 * @param {string} format 格式，默认：YYYY-MM-DD HH:mm:ss
 * @returns {string} 格式化后的时间
 */
export function formatDateTime(time, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!time) return '未知'
  return dayjs(time).format(format)
}

/**
 * 格式化日期
 * @param {string|Date} date 日期
 * @param {string} format 格式，默认：YYYY-MM-DD
 * @returns {string} 格式化后的日期
 */
export function formatDate(date, format = 'YYYY-MM-DD') {
  if (!date) return '未知'
  return dayjs(date).format(format)
}

/**
 * 获取时间范围
 * @param {string} type 类型：today|week|month|year
 * @returns {[Date, Date]} 开始时间和结束时间
 */
export function getTimeRange(type) {
  const now = dayjs()
  switch (type) {
    case 'today':
      return [now.startOf('day'), now.endOf('day')]
    case 'week':
      return [now.startOf('week'), now.endOf('week')]
    case 'month':
      return [now.startOf('month'), now.endOf('month')]
    case 'year':
      return [now.startOf('year'), now.endOf('year')]
    default:
      return [now, now]
  }
} 