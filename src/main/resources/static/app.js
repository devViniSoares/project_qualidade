'use strict';
const $ = id => document.getElementById(id);
let authorization = '', me = null, csrf = null, resources = [];
const profiles = {ADMIN:'Administrador', GESTOR:'Gestor', USUARIO:'Usuário'};
function message(text, error=false) { $('message').textContent=text; $('message').classList.toggle('error',error); $('message').hidden=false; }
async function token() { csrf=await (await fetch('/api/csrf')).json(); }
async function api(path, method='GET', body) {
  const headers = {'X-Requested-With':'XMLHttpRequest'};
  if (authorization) headers.Authorization=authorization;
  if (method!=='GET') { if(!csrf) await token(); headers[csrf.headerName]=csrf.token; }
  if(body!==undefined) headers['Content-Type']='application/json';
  const response=await fetch('/api'+path,{method,headers,body:body===undefined?undefined:JSON.stringify(body)});
  if(!response.ok) {
    let data={}; try { data=await response.json(); } catch {}
    throw new Error(response.status===401?'E-mail ou senha inválidos.':data.detail||(response.status===403?'Você não tem permissão ou sua sessão expirou. Entre novamente.':'Não foi possível concluir a operação.'));
  }
  return response.status===204?null:response.json();
}
function text(tag, value, className) { const e=document.createElement(tag); e.textContent=value; if(className)e.className=className; return e; }
function action(label, handler, className='text-button') { const b=text('button',label,className); b.type='button'; b.addEventListener('click',()=>run(handler,b)); return b; }
async function run(fn, button) { if(button)button.disabled=true; try { await fn(); } catch(e) { message(e.message,true); } finally { if(button)button.disabled=false; } }
function fields(form) { return Object.fromEntries(new FormData(form)); }
function manager() { return me && me.perfil!=='USUARIO'; }
async function enter(email, senha) {
  authorization='Basic '+btoa(String.fromCharCode(...new TextEncoder().encode(email+':'+senha)));
  try { me=await api('/me'); } catch(e) { authorization=''; throw e; }
  $('auth').hidden=true; $('app').hidden=false; $('logout').hidden=false;
  $('identity').textContent=me.nome+' · '+profiles[me.perfil];
  $('new-resource').hidden=!manager(); $('all-bookings-label').hidden=!manager(); $('admin').hidden=me.perfil!=='ADMIN';
  await refresh();
}
async function refresh() {
  const own=await api('/reservas');
  const all=await api('/recursos');
  $('stat-total').textContent=all.length;
  $('stat-active').textContent=all.filter(r=>r.ativo).length;
  $('stat-bookings').textContent=own.filter(r=>r.status==='ATIVA').length;
  await Promise.all([renderResources(),renderBookings()]);
}
async function renderResources() {
  const query=new URLSearchParams({busca:$('search').value,tipo:$('type-filter').value});
  if(!query.get('tipo'))query.delete('tipo');
  resources=await api('/recursos?'+query);
  $('resources').replaceChildren(); $('resource-empty').hidden=resources.length>0;
  for(const r of resources) {
    const card=text('article','','resource-card'), top=text('div','','card-top');
    top.append(text('span',r.tipo==='SALA'?'▦':'⌘','resource-icon'),text('span',r.ativo?'Habilitado':'Desativado','badge'+(r.ativo?'':' inactive')));
    card.append(top,text('h3',r.nome),text('p',r.localizacao+' · '+(r.tipo==='SALA'?'Sala':'Equipamento')),
      text('p','Capacidade: '+r.capacidade),text('p',r.descricao||'Pronto para o seu próximo projeto.','description'));
    const actions=text('div','','card-actions');
    if(r.ativo)actions.append(action('Reservar  ↗',()=>openBooking(r),'primary'));
    if(manager()) {
      actions.append(action('Editar',()=>openResource(r)));
      actions.append(action('Excluir',async()=>{ if(confirm('Excluir '+r.nome+'? Recursos com histórico devem ser desativados.')) { await api('/recursos/'+r.id,'DELETE'); message('Recurso excluído.'); await refresh(); } }));
    }
    card.append(actions); $('resources').append(card);
  }
}
const dateFormat=new Intl.DateTimeFormat('pt-BR',{dateStyle:'short',timeStyle:'short'});
async function renderBookings() {
  const rows=await api('/reservas?todas='+$('all-bookings').checked);
  $('bookings').replaceChildren(); $('booking-empty').hidden=rows.length>0;
  for(const r of rows) {
    const tr=document.createElement('tr'), name=document.createElement('td');
    name.append(text('strong',r.recursoNome),text('small',r.finalidade));
    const period=document.createElement('td');period.append(text('span',dateFormat.format(new Date(r.inicio))),text('small','até '+dateFormat.format(new Date(r.fim))));
    const status=document.createElement('td');status.append(text('span',r.status==='ATIVA'?'Ativa':'Cancelada','badge'+(r.status==='ATIVA'?'':' inactive')));
    const controls=document.createElement('td');
    if(r.status==='ATIVA') controls.append(action('Cancelar',async()=>{ if(confirm('Cancelar esta reserva e liberar o horário?')) { await api('/reservas/'+r.id+'/cancelamento','POST'); message('Reserva cancelada. O horário foi liberado.'); await refresh(); } }));
    tr.append(name,text('td',r.usuarioNome),period,status,controls); $('bookings').append(tr);
  }
}
function openResource(r) {
  const form=$('resource-form'); form.reset(); $('resource-title').textContent=r?'Editar recurso':'Novo recurso';
  form.elements.id.value=r?r.id:'';
  if(r)for(const key of ['nome','tipo','localizacao','capacidade','descricao'])form.elements[key].value=r[key];
  form.elements.ativo.checked=r?r.ativo:true; $('resource-dialog').showModal();
}
function openBooking(r) {
  $('booking-form').reset(); $('booking-form').elements.recursoId.value=r.id; $('booking-resource').textContent=r.nome+' · '+r.localizacao;
  $('availability').textContent=''; $('booking-dialog').showModal();
}
function bookingData() {
  const d=fields($('booking-form'));
  if(!d.inicio || !d.fim)throw new Error('Informe o início e o fim da reserva.');
  return {...d,recursoId:Number(d.recursoId),inicio:new Date(d.inicio).toISOString(),fim:new Date(d.fim).toISOString()};
}
function submit(id, fn) { $(id).addEventListener('submit',e=>{ e.preventDefault(); run(()=>fn(e.target),e.submitter); }); }
submit('login-form',async form=>{const d=fields(form);await enter(d.email,d.senha);form.reset();message('Bem-vindo. Encontre o recurso para sua próxima atividade.');});
submit('signup-form',async form=>{const d=fields(form);await api('/cadastro','POST',d);await enter(d.email,d.senha);form.reset();message('Conta criada. Você já pode reservar.');});
submit('resource-form',async form=>{const d=fields(form),id=d.id;delete d.id;d.capacidade=Number(d.capacidade);d.ativo=form.elements.ativo.checked;await api('/recursos'+(id?'/'+id:''),id?'PUT':'POST',d);$('resource-dialog').close();message('Recurso salvo.');await refresh();});
submit('booking-form',async()=>{await api('/reservas','POST',bookingData());$('booking-dialog').close();message('Reserva confirmada. Confira os detalhes na sua agenda.');await refresh();});
submit('user-form',async form=>{await api('/usuarios','POST',fields(form));form.reset();message('Integrante cadastrado.');});
$('check-availability').addEventListener('click',()=>run(async()=>{const d=bookingData(),q=new URLSearchParams({inicio:d.inicio,fim:d.fim});const r=await api('/recursos/'+d.recursoId+'/disponibilidade?'+q);$('availability').textContent=r.disponivel?'Horário disponível neste momento. Confirme para reservar.':'O recurso está indisponível neste horário.';},$('check-availability')));
$('new-resource').addEventListener('click',()=>openResource());
$('refresh').addEventListener('click',()=>run(refresh,$('refresh')));
$('type-filter').addEventListener('change',()=>run(renderResources));
$('search').addEventListener('change',()=>run(renderResources));
$('all-bookings').addEventListener('change',()=>run(renderBookings));
$('logout').addEventListener('click',()=>run(async()=>{await fetch('/logout',{method:'POST',headers:{[csrf.headerName]:csrf.token}});authorization='';me=null;csrf=null;location.reload();}));
document.querySelectorAll('.close-dialog').forEach(b=>b.addEventListener('click',()=>b.closest('dialog').close()));
run(token);
