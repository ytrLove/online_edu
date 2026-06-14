/**
 * 格式化时间
 * @param {(Object|string|number)} time
 * @param {string} pattern
 * @returns {string}
 */
export function parseTime(time, pattern) {
  if (arguments.length === 0 || !time) {
    return null
  }
  const format = pattern || '{y}-{m}-{d} {h}:{i}:{s}'
  let date
  if (typeof time === 'object') {
    date = time
  } else {
    if ((typeof time === 'string') && (/^[0-9]+$/.test(time))) {
      time = parseInt(time)
    } else if (typeof time === 'string') {
      time = time.replace(new RegExp(/-/gm), '/').replace('T', ' ').replace(new RegExp(/\.[\d]{3}/gm), '')
    }
    if ((typeof time === 'number') && (time.toString().length === 10)) {
      time = time * 1000
    }
    date = new Date(time)
  }
  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  }
  const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
    let value = formatObj[key]
    // 注意: getDay() 返回 0 是星期日
    if (key === 'a') { return ['日', '一', '二', '三', '四', '五', '六'][value] }
    if (result.length > 0 && value < 10) {
      value = '0' + value
    }
    return value || 0
  })
  return time_str
}

/**
 * 格式化时间为友好显示
 * @param {number} time
 * @param {string} option
 * @returns {string}
 */
export function formatTime(time, option) {
  if (('' + time).length === 10) {
    time = parseInt(time) * 1000
  } else {
    time = +time
  }
  const d = new Date(time)
  const now = Date.now()

  const diff = (now - d) / 1000

  if (diff < 30) {
    return '刚刚'
  } else if (diff < 3600) {
    // less 1 hour
    return Math.ceil(diff / 60) + '分钟前'
  } else if (diff < 3600 * 24) {
    return Math.ceil(diff / 3600) + '小时前'
  } else if (diff < 3600 * 24 * 2) {
    return '1天前'
  }
  if (option) {
    return parseTime(time, option)
  } else {
    return (
      d.getMonth() +
      1 +
      '月' +
      d.getDate() +
      '日' +
      d.getHours() +
      '时' +
      d.getMinutes() +
      '分'
    )
  }
}

/**
 * 数字格式化
 * like 10000 => 10k
 * @param {number} num
 * @param {number} digits
 * @returns {string}
 */
export function numberFormatter(num, digits) {
  const si = [
    { value: 1E18, symbol: 'E' },
    { value: 1E15, symbol: 'P' },
    { value: 1E12, symbol: 'T' },
    { value: 1E9, symbol: 'G' },
    { value: 1E6, symbol: 'M' },
    { value: 1E3, symbol: 'k' }
  ]
  for (let i = 0; i < si.length; i++) {
    if (num >= si[i].value) {
      return (num / si[i].value).toFixed(digits).replace(/\.0+$|(\.[0-9]*[1-9])0+$/, '$1') + si[i].symbol
    }
  }
  return num.toString()
}

/**
 * 金额格式化
 * @param {number} num
 * @param {number} digits
 * @param {string} prefix
 * @param {string} suffix
 * @returns {string}
 */
export function toThousandFilter(num, digits, prefix, suffix) {
  digits = digits || 2
  num = parseFloat(num)
  return (
    (prefix || '') +
    ((Math.round(num * Math.pow(10, digits)) / Math.pow(10, digits)).toFixed(digits) + '').replace(
      /\B(?=(\d{3})+(?!\d))/g,
      ','
    ) +
    (suffix || '')
  )
}

/**
 * 首字母大写
 * @param {string} string
 * @returns {string}
 */
export function capitalizeFirstLetter(string) {
  return string.charAt(0).toUpperCase() + string.slice(1)
}

/**
 * 深拷贝
 * @param {Object} source
 * @returns {Object}
 */
export function deepClone(source) {
  if (!source && typeof source !== 'object') {
    throw new Error('error arguments', 'deepClone')
  }
  const targetObj = source.constructor === Array ? [] : {}
  Object.keys(source).forEach(keys => {
    if (source[keys] && typeof source[keys] === 'object') {
      targetObj[keys] = deepClone(source[keys])
    } else {
      targetObj[keys] = source[keys]
    }
  })
  return targetObj
}

/**
 * 防抖函数
 * @param {Function} func
 * @param {number} wait
 * @param {boolean} immediate
 * @returns {Function}
 */
export function debounce(func, wait, immediate) {
  let timeout, args, context, timestamp, result

  const later = function() {
    const last = Date.now() - timestamp

    if (last < wait && last > 0) {
      timeout = setTimeout(later, wait - last)
    } else {
      timeout = null
      if (immediate) {
        const callNow = !timeout
        timeout = setTimeout(() => {
          timeout = null
        }, wait)
        if (callNow) result = func.apply(context, args)
      }
    }
  }

  return function(...args) {
    context = this
    timestamp = Date.now()
    const callNow = immediate && !timeout
    if (!timeout) timeout = setTimeout(later, wait)
    if (callNow) result = func.apply(context, args)
    return result
  }
}

/**
 * 节流函数
 * @param {Function} func
 * @param {number} wait
 * @returns {Function}
 */
export function throttle(func, wait) {
  let previous = 0
  return function(...args) {
    const now = Date.now()
    if (now - previous > wait) {
      previous = now
      return func.apply(this, args)
    }
  }
}

/**
 * 树形数据转换
 * @param {Array} data
 * @param {string} id
 * @param {string} pid
 * @param {string} children
 * @returns {Array}
 */
export function treeify(data, id = 'id', pid = 'pid', children = 'children') {
  const tree = []
  const lookup = {}
  data.forEach(item => {
    lookup[item[id]] = { ...item, [children]: [] }
    const treeItem = lookup[item[id]]
    if (item[pid] === null || item[pid] === undefined || item[pid] === '') {
      tree.push(treeItem)
    } else {
      if (!lookup[item[pid]]) {
        lookup[item[pid]] = { [children]: [] }
      }
      lookup[item[pid]][children].push(treeItem)
    }
  })
  return tree
}

/**
 * 判断是否为空
 * @param {*} val
 * @returns {boolean}
 */
export function isEmpty(val) {
  if (val === null || val === undefined) return true
  if (Array.isArray(val)) return val.length === 0
  if (typeof val === 'object') return Object.keys(val).length === 0
  if (typeof val === 'string') return val.trim().length === 0
  return false
}

/**
 * 生成UUID
 * @returns {string}
 */
export function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = (Math.random() * 16) | 0
    const v = c === 'x' ? r : (r & 0x3) | 0x8
    return v.toString(16)
  })
}

/**
 * 格式化文件大小
 * @param {number} size
 * @returns {string}
 */
export function formatFileSize(size) {
  if (size < 1024) {
    return size + ' B'
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + ' KB'
  } else if (size < 1024 * 1024 * 1024) {
    return (size / (1024 * 1024)).toFixed(2) + ' MB'
  } else {
    return (size / (1024 * 1024 * 1024)).toFixed(2) + ' GB'
  }
}

/**
 * 获取文件扩展名
 * @param {string} filename
 * @returns {string}
 */
export function getFileExt(filename) {
  return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase()
}

/**
 * 获取文件类型图标
 * @param {string} filename
 * @returns {string}
 */
export function getFileIcon(filename) {
  const ext = getFileExt(filename)
  const iconMap = {
    // 图片
    jpg: 'Picture',
    jpeg: 'Picture',
    png: 'Picture',
    gif: 'Picture',
    // 文档
    doc: 'Document',
    docx: 'Document',
    xls: 'Document',
    xlsx: 'Document',
    ppt: 'Document',
    pptx: 'Document',
    pdf: 'Document',
    txt: 'Document',
    // 压缩包
    zip: 'Folder',
    rar: 'Folder',
    '7z': 'Folder',
    // 其他
    exe: 'Monitor',
    default: 'Document'
  }
  return iconMap[ext] || iconMap.default
} 