import Vue from 'vue'
import moment from 'moment'
const formats = {
  date: 'YYYY-MM-DD',
  datetime: 'YYYY-MM-DD HH:mm:ss',
  datetimem: 'YYYY-MM-DD HH:mm',
  month: 'MM-DD',
  year: 'YYYY'
}
Vue.filter('moment', function(value, format = 'date') {
  if (!value) {
    return ''
  }
  const pattern = formats[format]
  return moment(Number(value)).format(pattern)
})
