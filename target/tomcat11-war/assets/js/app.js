document.addEventListener('DOMContentLoaded', () => {
 const toggle = document.querySelector('.menu-toggle');
 toggle?.addEventListener('click', () => {
  const open = document.querySelector('.site-header').classList.toggle('menu-open');
  toggle.setAttribute('aria-expanded', String(open));
 });
 document.querySelectorAll('img[data-cover]').forEach(image => {
  image.addEventListener('error', () => { if(!image.dataset.fallback){image.dataset.fallback='1';image.src=image.dataset.placeholder;} });
 });
 document.querySelectorAll('form[data-confirm]').forEach(form => {
  form.addEventListener('submit', event => { if(!window.confirm(form.dataset.confirm)) event.preventDefault(); });
 });
 document.querySelectorAll('[data-quantity]').forEach(button => {
  button.addEventListener('click', () => {
   const input=button.closest('.quantity-control').querySelector('input');
   const value=Number(input.value)+Number(button.dataset.quantity);
   input.value=String(Math.min(Number(input.max),Math.max(Number(input.min),value)));
   input.dispatchEvent(new Event('change',{bubbles:true}));
  });
 });
 document.querySelectorAll('[data-password]').forEach(button => {
  button.addEventListener('click', () => {
   const input=button.parentElement.querySelector('input');
   const visible=input.type==='password';input.type=visible?'text':'password';
   button.textContent=visible?'Ẩn':'Hiện';button.setAttribute('aria-pressed',String(visible));
  });
 });
});