function campusAiNavigate() { if (typeof openGemini === 'function') { openGemini(); } else { location.href = '/chats.html?ai=1'; } }
/* CampusConnect real client */
let CURRENT_USER_ID = null;
let CURRENT_USER = null;

function escapeHtml(str) {
    const d = document.createElement('div');
    d.textContent = str || '';
    return d.innerHTML;
}

function timeAgo(iso) { if (!iso) return ''; const s = Math.floor((Date.now() - new Date(iso).getTime()) / 1000); if (s < 60) return 'now'; if (s < 3600) return Math.floor(s / 60) + 'm'; if (s < 86400) return Math.floor(s / 3600) + 'h'; if (s < 604800) return Math.floor(s / 86400) + 'd'; return new Date(iso).toLocaleDateString(); }

function withinEditWindow(iso) { return !!iso && (Date.now() - new Date(iso).getTime()) < 20 * 60 * 1000; }

function profileHref(id) { return '/profile.html?userId=' + encodeURIComponent(id); }

function goBack(fallback = '/index.html') { if (history.length > 1) { history.back(); } else { location.href = fallback; } }

function eyeIcon(open) { return open ? '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6S2 12 2 12Z"/><circle cx="12" cy="12" r="2.5"/></svg>' : '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M3 3l18 18"/><path d="M10.6 6.2A10.9 10.9 0 0 1 12 6c6.5 0 10 6 10 6a17.8 17.8 0 0 1-3.2 3.8M6.2 6.7C3.5 8.5 2 12 2 12s3.5 6 10 6c1.3 0 2.5-.3 3.6-.7"/></svg>' }

function togglePw(id, btn) {
    const input = document.getElementById(id);
    if (!input) return;
    const showing = input.type === 'text';
    input.type = showing ? 'password' : 'text';
    if (btn) {
        btn.innerHTML = eyeIcon(!showing);
        btn.setAttribute('aria-label', showing ? 'Show password' : 'Hide password');
        btn.title = showing ? 'Show password' : 'Hide password';
    }
}

function applyTheme(theme) {
    document.documentElement.dataset.theme = theme;
    localStorage.setItem('cc_theme', theme);
    const b = document.getElementById('cc-theme-toggle');
    if (b) b.textContent = theme === 'dark' ? '☀' : '☾';
}

function installThemeToggle() {
    const saved = localStorage.getItem('cc_theme') || 'light';
    document.documentElement.dataset.theme = saved;
    if (document.getElementById('cc-theme-toggle')) return;
    const b = document.createElement('button');
    b.id = 'cc-theme-toggle';
    b.className = 'theme-toggle';
    b.title = 'Toggle dark/light mode';
    b.setAttribute('aria-label', 'Toggle dark mode');
    b.textContent = saved === 'dark' ? '☀' : '☾';
    b.onclick = () => applyTheme(document.documentElement.dataset.theme === 'dark' ? 'light' : 'dark');
    const host = document.querySelector('.top-header,.chat-toolbar,.view-title');
    if (host) {
        const actions = host.querySelector('.header-icons,.chat-top-actions');
        if (actions) actions.appendChild(b);
        else host.appendChild(b);
    } else { document.body.appendChild(b) }
}

function googleMapsCurrentLocation() {
    if (!navigator.geolocation) { toast('Location is not supported on this device', true); return; }
    navigator.geolocation.getCurrentPosition(pos => {
        const url = 'https://www.google.com/maps?q=' + pos.coords.latitude + ',' + pos.coords.longitude;
        window.open(url, '_blank', 'noopener');
    }, () => toast('Allow location access to open your current location in Google Maps', true), { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 });
}

function toast(message, isError = false) {
    const host = document.getElementById('toast-host');
    if (!host) return;
    const e = document.createElement('div');
    e.className = 'toast' + (isError ? ' err' : '');
    e.textContent = message;
    host.appendChild(e);
    setTimeout(() => e.remove(), 2800);
}
async function apiFetch(path, options = {}) { try { const headers = options.body instanceof FormData ? {} : { 'Content-Type': 'application/json' }; const res = await fetch(path, { credentials: 'same-origin', headers, ...options }); let data = null; const text = await res.text(); if (text) { try { data = JSON.parse(text) } catch { data = { message: text } } } return { ok: res.ok, offline: false, status: res.status, data }; } catch (e) { return { ok: false, offline: true, data: null }; } }
const ICONS = { plug: '<svg viewBox="0 0 100 100" fill="none" stroke="currentColor"><path d="M35 45V25M65 45V25M30 45h40v15a20 20 0 01-40 0V45zM50 75v10"/></svg>', posts: '<svg viewBox="0 0 100 100" fill="none" stroke="currentColor"><rect x="16" y="24" width="68" height="52" rx="8"/><path d="M28 40h44M28 52h30"/></svg>', search: '<svg viewBox="0 0 100 100" fill="none" stroke="currentColor"><circle cx="42" cy="42" r="22"/><path d="M60 60l18 18"/></svg>', chats: '<svg viewBox="0 0 100 100" fill="none" stroke="currentColor"><path d="M17 25h48a7 7 0 017 7v27a7 7 0 01-7 7H43L27 79V66h-2a8 8 0 01-8-8V25Z"/><path d="M41 19h36a7 7 0 017 7v25a7 7 0 01-7 7h-3"/><path d="M31 39h23M31 49h18"/></svg>', bell: '<svg viewBox="0 0 100 100" fill="none" stroke="currentColor"><path d="M50 20a16 16 0 00-16 16c0 18-8 24-8 24h48s-8-6-8-24a16 16 0 00-16-16zM42 66a8 8 0 0016 0"/></svg>', bag: '<svg viewBox="0 0 100 100" fill="none" stroke="currentColor"><path d="M24 38l6-16h40l6 16M24 38h52v34a4 4 0 01-4 4H28a4 4 0 01-4-4V38zM40 38a10 10 0 0020 0"/></svg>', spark: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><path d="M12 2.5c1 5.8 3.7 8.5 9.5 9.5-5.8 1-8.5 3.7-9.5 9.5-1-5.8-3.7-8.5-9.5-9.5 5.8-1 8.5-3.7 9.5-9.5Z"/></svg>' };
ICONS.home = '<svg viewBox="0 0 100 100" fill="none" stroke="currentColor"><path d="M15 45 50 15l35 30"/><path d="M25 40v45h50V40"/><path d="M40 85V58h20v27"/></svg>';
ICONS.profile = '<svg viewBox="0 0 100 100" fill="none" stroke="currentColor"><circle cx="50" cy="32" r="16"/><path d="M20 86c2-20 14-30 30-30s28 10 30 30"/></svg>';

function emptyStateHTML({ icon, title, body, ctaLabel, ctaOnclick }) { return `<div class="empty-state"><div class="empty-illo">${icon}</div><div class="empty-title">${escapeHtml(title)}</div><div class="empty-body">${escapeHtml(body)}</div>${ctaLabel?`<button class="btn btn-primary btn-auto empty-cta" onclick="${ctaOnclick}">${escapeHtml(ctaLabel)}</button>`:''}</div>`}
function notConnectedStateHTML(){return emptyStateHTML({icon:ICONS.plug,title:'Backend unavailable',body:'CampusConnect could not reach the Spring Boot server. Start the app and make sure MySQL is running.'});}
function skeletonPosts(n){return Array.from({length:n},()=>'<div class="skeleton-post"><div class="skeleton skeleton-line" style="width:130px"></div><div class="skeleton skeleton-line" style="width:90%"></div><div class="skeleton skeleton-line" style="width:60%"></div></div>').join('');}
async function loadCurrentUser(){const r=await apiFetch('/api/auth/me');if(r.ok&&r.data?.authenticated){CURRENT_USER_ID=r.data.id;const u=await apiFetch('/api/users/'+CURRENT_USER_ID);if(u.ok)CURRENT_USER=u.data;return CURRENT_USER;}CURRENT_USER_ID=null;CURRENT_USER=null;return null;}
async function requireLogin(){const u=await loadCurrentUser();if(!u){window.location.href='/login.html';return null;}return u;}
async function logout(){await apiFetch('/api/auth/logout',{method:'POST'});window.location.href='/login.html';}
function setPostNavHidden(hidden){document.querySelectorAll('.nav-post').forEach(n=>n.classList.toggle('hidden',!!hidden));}
function openComposer(){document.getElementById('composer-modal')?.classList.add('show');setPostNavHidden(true)}
function closeComposer(){document.getElementById('composer-modal')?.classList.remove('show');setPostNavHidden(false)}
async function moderatePostBeforePublish(content,file){
 // AI moderation is optional. This build does not expose /api/ai/moderate-post,
 // so moderation must never prevent a normal post from being saved.
 return {ok:true,blocked:false,message:'OK'};
}
async function submitPost(){
 const text=document.getElementById('composer-text')?.value.trim(), file=document.getElementById('composer-file')?.files?.[0];
 if(text.length>280){toast('Posts are limited to 280 characters',true);return} if(!text&&!file){toast('Write something or choose media',true);return}
 const btn=document.getElementById('composer-submit-btn');if(btn)btn.disabled=true;
 const moderation=await moderatePostBeforePublish(text,file);if(!moderation.ok){toast(moderation.message||'Post not supported',true);if(btn)btn.disabled=false;return;}
 let imageUrl='',videoUrl='';
 if(file){
   if(file.size>200*1024*1024){toast('Videos must be 200MB or smaller',true);if(btn)btn.disabled=false;return}
   if(file.type.startsWith('video/')){const v=document.createElement('video');v.preload='metadata';v.src=URL.createObjectURL(file);await new Promise(resolve=>v.onloadedmetadata=resolve);if(v.duration>120){toast('Videos must be 2 minutes or shorter',true);if(btn)btn.disabled=false;return}}
   const up=await apiFetch('/api/upload',{method:'POST',body:(()=>{const f=new FormData();f.append('file',file);return f})()});
   if(!up.ok){toast(up.data?.message||'Upload failed',true);if(btn)btn.disabled=false;return}
   if(up.data.type?.startsWith('video/'))videoUrl=up.data.url;else imageUrl=up.data.url;
 }
 const r=await apiFetch('/api/posts',{method:'POST',body:JSON.stringify({content:text||'(media)',imageUrl,videoUrl})});if(btn)btn.disabled=false;
 if(r.ok){document.getElementById('composer-text').value='';if(document.getElementById('composer-file'))document.getElementById('composer-file').value='';closeComposer();toast('Posted');window.onPostCreated?.(r.data)}else toast(r.data?.message||'Could not create post',true);
}
async function toggleLike(id,btn){const liked=btn.dataset.liked==='true';const r=await apiFetch(`/api/posts/${id}/${liked?'unlike':'like'}`,{method:'POST'});if(r.ok){btn.dataset.liked=String(r.data.liked);btn.classList.toggle('liked',r.data.liked);btn.querySelector('.like-symbol').textContent=r.data.liked?'♥':'♡';btn.querySelector('.like-count').textContent=r.data.likeCount}else toast(r.data?.message||'Could not update like',true)}
async function showComments(postId){
 const r=await apiFetch('/api/comments/post/'+postId);if(!r.ok){toast('Could not load comments',true);return}
 const lines=r.data.map(c=>`<div class="comment-row"><b>@${escapeHtml(c.username)}</b><span>${escapeHtml(c.content)}</span><small>${timeAgo(c.createdAt)}</small></div>`).join('')||'<div class="comment-row">No comments yet.</div>';
 const text=prompt('Comments:\n\n'+r.data.map(c=>'@'+c.username+': '+c.content).join('\n')+'\n\nWrite a new comment (Cancel to close):');
 if(text?.trim()){const a=await apiFetch('/api/comments/post/'+postId,{method:'POST',body:JSON.stringify({content:text.trim()})});if(a.ok){toast('Comment added');window.onPostCreated?.()}else toast(a.data?.message||'Could not comment',true)}
}
function iconSvg(type){
 if(type==='edit') return '<svg viewBox="0 0 24 24"><path d="M4 20h4L19 9a2.1 2.1 0 0 0-3-3L5 17l-1 3Z"/><path d="m14.5 7.5 2 2"/></svg>';
 if(type==='minus') return '<svg viewBox="0 0 24 24"><path d="M5 12h14"/></svg>';
 if(type==='delete') return '<svg viewBox="0 0 24 24"><path d="M4 7h16"/><path d="M9 7V4h6v3"/><path d="M7 7l1 13h8l1-13"/><path d="M10 11v5M14 11v5"/></svg>';
 return '<svg viewBox="0 0 24 24"><path d="M12 5v14M5 12h14"/></svg>';
}

async function toggleFollowUser(id,btn){
 if(btn.disabled)return; btn.disabled=true;
 const following=btn.dataset.following==='true';
 const r=await apiFetch('/api/follows/'+id,{method:following?'DELETE':'POST'});
 if(r.ok){
   const next=!following;
   btn.dataset.following=String(next);
   btn.classList.toggle('following',next);
   const label=next?'Following':'Follow';
   const span=btn.querySelector('span');if(span)span.textContent=label;
   btn.title=next?'Open profile for follow options':'Follow @'+(btn.dataset.username||'user');
   document.querySelectorAll(`.follow-post-btn[data-user-id="${id}"]`).forEach(x=>{
     x.dataset.following=String(next);x.classList.toggle('following',next);
     const s=x.querySelector('span');if(s)s.textContent=label;
     x.title=next?'Open profile for follow options':'Follow @'+(x.dataset.username||'user');
   });
   if(next) toast('Following @'+(btn.dataset.username||'user')+' · open their profile for more options');
   else toast('Unfollowed @'+(btn.dataset.username||'user'));
 }else toast(r.data?.message||'Could not update follow',true);
 btn.disabled=false;
}
async function followFromPost(id,btn){return toggleFollowUser(id,btn)}
function savePost(id){const key='cc_saved_posts';const set=new Set(JSON.parse(localStorage.getItem(key)||'[]').map(Number));if(set.has(Number(id)))set.delete(Number(id));else set.add(Number(id));localStorage.setItem(key,JSON.stringify([...set]));toast(set.has(Number(id))?'Post saved':'Removed from saved');return set.has(Number(id));}
function isSavedPost(id){return new Set(JSON.parse(localStorage.getItem('cc_saved_posts')||'[]').map(Number)).has(Number(id));}

function postCardHTML(p){
 const initial=(p.authorUsername||'?')[0].toUpperCase();
 const liked=!!p.liked;
 const owner=Number(p.authorId)===Number(CURRENT_USER_ID);
 const following=!!p.followingAuthor;
 const canEdit=owner&&withinEditWindow(p.createdAt);
 const followButton=!owner?`<button class="follow-inline follow-post-btn ${following?'following':''}" data-user-id="${p.authorId}" data-username="${escapeHtml(p.authorUsername)}" data-following="${following}" onclick="toggleFollowUser(${p.authorId},this)" title="${following?'Unfollow':'Follow'} @${escapeHtml(p.authorUsername)}">${iconSvg(following?'minus':'plus')}<span>${following?'Unfollow':'Follow'}</span></button>`:'';
 return `<article class="post-card" data-post-id="${p.id}" data-post-author-id="${p.authorId}" data-following="${following}">
   <div class="post-head">
     <a class="avatar sm avatar-link" href="${profileHref(p.authorId)}" aria-label="Open @${escapeHtml(p.authorUsername)} profile">${p.authorProfilePictureUrl?`<img src="${escapeHtml(p.authorProfilePictureUrl)}" alt="">`:escapeHtml(initial)}</a>
    <div class="post-head-meta"><div class="author-line"><a class="post-author-name profile-link" href="${profileHref(p.authorId)}">${escapeHtml(p.authorFullName||p.authorName||p.authorUsername||"Student")}</a><a class="post-username profile-link" href="${profileHref(p.authorId)}">@${escapeHtml(p.authorUsername)}</a><span class="post-timestamp" data-created-at="${escapeHtml(p.createdAt)}">· ${timeAgo(p.createdAt)}</span>${followButton?` ${followButton}`:''}</div></div>
     ${owner?`<div class="post-owner-actions">${canEdit?`<button class="post-icon-btn" onclick="editPost(${p.id})" aria-label="Edit post" title="Edit post">${iconSvg('edit')}</button>`:''}<button class="post-icon-btn danger" onclick="deletePost(${p.id})" aria-label="Delete post" title="Delete post">${iconSvg('delete')}</button></div>`:''}
   </div>
   <div class="post-body">${escapeHtml(p.content)}</div>
   ${p.imageUrl?`<img class="post-media" src="${escapeHtml(p.imageUrl)}" alt="Post media" loading="lazy">`:''}
   ${p.videoUrl?`<video class="post-media feed-video" controls muted playsinline preload="metadata" src="${escapeHtml(p.videoUrl)}"></video>`:''}
   <div class="post-actions">
    <button class="post-action post-comment-action" onclick="showComments(${p.id})" aria-label="Reply to post"><svg viewBox="0 0 24 24"><path d="M20 11.5a8.3 8.3 0 0 1-8.5 8.4 8.6 8.6 0 0 1-4.1-1L3 21l1.8-4a8.3 8.3 0 0 1-1.7-4.7A8.4 8.4 0 0 1 11.5 4 8.4 8.4 0 0 1 20 11.5Z"/></svg><span>${p.commentCount||0}</span></button>
    <button class="post-action post-share-action" onclick="sharePost(${p.id})" aria-label="Share post"><svg viewBox="0 0 24 24"><path d="M7 7h10a4 4 0 0 1 4 4v1M7 7l3-3M7 7l3 3M17 17H7a4 4 0 0 1-4-4v-1m14 5-3 3m3-3-3-3"/></svg><span>Share</span></button>
    <button class="post-action ${liked?'liked':''}" data-liked="${liked}" onclick="toggleLike(${p.id},this)" aria-label="Like post"><span class="like-symbol">${liked?'♥':'♡'}</span><span class="like-count">${p.likeCount||0}</span></button>
    <span class="post-action post-views" title="Views" aria-label="Views"><svg viewBox="0 0 24 24"><path d="M3 19V5m0 14h18M7 16v-4m5 4V8m5 8v-7"/></svg><span>${Number(p.viewCount||0)}</span></span>
    <button class="post-action ${isSavedPost(p.id)?'saved':''}" onclick="savePost(${p.id});this.classList.toggle('saved',isSavedPost(${p.id}));this.querySelector('.save-label').textContent=isSavedPost(${p.id})?'Saved':'Save'" aria-label="Save post"><svg viewBox="0 0 24 24"><path d="M6 4.5A1.5 1.5 0 0 1 7.5 3h9A1.5 1.5 0 0 1 18 4.5V21l-6-4-6 4V4.5Z"/></svg><span class="save-label">${isSavedPost(p.id)?'Saved':'Save'}</span></button>
   </div>
 </article>`;
}

async function sharePost(id){
 const url=location.origin+'/index.html#post-'+id;
 try{await navigator.clipboard.writeText(url);toast('Post link copied');}
 catch{toast('Post link: '+url);}
}

async function editPost(id){
 const card=document.querySelector(`[data-post-id="${id}"]`),body=card?.querySelector('.post-body'); if(!card)return; const created=card.querySelector('.post-timestamp')?.dataset?.createdAt; // backend remains authoritative for the 20-minute limit
 const next=prompt('Edit your post',body?.textContent||'');
 if(next===null)return;
 if(!next.trim()){toast('Post content cannot be empty',true);return;}
 const r=await apiFetch('/api/posts/'+id,{method:'PUT',body:JSON.stringify({content:next.trim()})});
 if(r.ok){toast('Post updated');if(body)body.textContent=r.data.content}else toast(r.data?.message||'Could not edit post',true);
}

async function deletePost(id){
 if(!confirm('Delete this post permanently?'))return;
 const r=await apiFetch('/api/posts/'+id,{method:'DELETE'});
 if(r.ok){document.querySelector(`[data-post-id="${id}"]`)?.remove();toast('Post deleted')}else toast(r.data?.message||'Could not delete post',true);
}

function installSharedBottomNav(){
 if(['auth','login','register','forgot-password'].includes(document.body.dataset.page))return;
 document.querySelectorAll('.bottom-nav').forEach(n=>n.remove());
 const nav=document.createElement('nav');nav.className='bottom-nav cc-shared-nav';
 nav.innerHTML=`<a class="nav-item" data-nav="home" href="/index.html">${ICONS.home}<span>Home</span></a>
 <a class="nav-item" data-nav="search" href="/search.html">${ICONS.search}<span>Search</span></a>
 <button class="nav-item nav-post" data-nav="post" onclick="location.href='/index.html?compose=1'" aria-label="Create post"><span class="post-plus"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 5v14M5 12h14"/></svg></span><span>Post</span></button>
 <a class="nav-item nav-chat-icon" data-nav="chats" href="/chats.html" aria-label="Open chats">${ICONS.chats}<span>Chats</span></a>
 <a class="nav-item nav-ai" data-nav="ai" href="/gemini.html" aria-label="Open Campus AI">${ICONS.spark}<span>Campus AI</span></a>
 <a class="nav-item" data-nav="profile" href="/profile.html" aria-label="Profile and settings">${ICONS.profile}<span>Profile</span></a>`;
 (document.getElementById('app') || document.body).appendChild(nav);
}

async function refreshNotificationBadges(){
 const r=await apiFetch('/api/notifications/unread-count'); if(!r.ok)return;
 const n=Number(r.data?.count||0), el=document.getElementById('cc-notification-badge');if(el){el.textContent=n>99?'99+':n;el.classList.toggle('show',n>0)}

}
function setupSharedNav(){installSharedBottomNav();document.querySelectorAll('.nav-item').forEach(x=>x.classList.toggle('active',x.dataset.nav===document.body.dataset.page));if(new URLSearchParams(location.search).get('compose')==='1')setPostNavHidden(true);refreshNotificationBadges();setInterval(refreshNotificationBadges,10000);}

function installTopProfile(){
 if(['auth','login','register','forgot-password'].includes(document.body.dataset.page))return;
 const header=document.querySelector('.top-header');
 const title=document.querySelector('.view-title');
 const host=header||title;if(!host||host.querySelector('.cc-top-profile'))return;
 let actions=host.querySelector('.header-icons');
 if(!actions){actions=document.createElement('div');actions.className='header-icons';actions.style.display='flex';actions.style.alignItems='center';actions.style.gap='6px';host.appendChild(actions)}
 const a=document.createElement('a');a.className='cc-top-profile';a.href='/profile.html';a.title='Profile settings';a.setAttribute('aria-label','Open profile settings');
 a.innerHTML=ICONS.profile;actions.appendChild(a);
}
function installUniversalBack(){
 if(['auth','login','register','forgot-password','home'].includes(document.body.dataset.page))return;
 if(document.querySelector('.cc-home-back')||document.querySelector('.page-back'))return;
 const main=document.querySelector('main')||document.getElementById('app'); if(!main)return;
 const wrap=document.createElement('div'); wrap.className='cc-home-back';
 const calls=document.body.dataset.page==='calls';
 wrap.innerHTML=`<button type="button" aria-label="Back">← ${calls?'Back to Chats':'Back to Home'}</button>`;
 wrap.querySelector('button').onclick=()=>{location.href=calls?'/chats.html':'/index.html'};
 main.prepend(wrap);
}
function installMobileBackSupport(){
 if(document.body.dataset.page!=='chats')return;
 window.addEventListener('popstate',()=>{
   if(document.body.dataset.page==='chats'&&document.getElementById('layout')?.classList.contains('open')){
     closeChat(true);
   }
 });
}
document.addEventListener('DOMContentLoaded',()=>{setupSharedNav();installThemeToggle();installTopProfile();installUniversalBack();installMobileBackSupport();});

async function registerPostView(id){try{await apiFetch('/api/posts/'+id+'/view',{method:'POST'});}catch(e){}}
function enableFeedVideoAutoplay(root=document){const videos=[...root.querySelectorAll('.feed-video')];if(!videos.length)return;if(!window.__ccVideoObserver){window.__ccVideoObserver=new IntersectionObserver(entries=>entries.forEach(e=>{const v=e.target;if(e.isIntersecting&&e.intersectionRatio>.55){v.play().catch(()=>{});}else v.pause();}),{threshold:[0,.55,.8]});}videos.forEach(v=>window.__ccVideoObserver.observe(v));root.querySelectorAll('[data-post-id]').forEach(card=>{if(card.dataset.viewRegistered)return;card.dataset.viewRegistered='true';const ob=new IntersectionObserver(es=>{if(es.some(e=>e.isIntersecting)){registerPostView(card.dataset.postId);ob.disconnect()}},{threshold:.5});ob.observe(card)});}
document.addEventListener('DOMContentLoaded',()=>enableFeedVideoAutoplay());

function refreshEditControls(){document.querySelectorAll('.post-card[data-post-id]').forEach(card=>{const ts=card.querySelector('.post-timestamp')?.dataset.createdAt;if(!ts||withinEditWindow(ts))return;card.querySelector('.post-icon-btn[aria-label="Edit post"]')?.remove();});document.querySelectorAll('[data-message-id]').forEach(w=>{const ts=w.querySelector('.bubble-meta')?.textContent;});}
setInterval(refreshEditControls,30000);