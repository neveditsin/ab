package com.nc.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.nc.events.Event;
import com.nc.events.Event.EventType;
import com.jcraft.jsch.*;

public class Ssher implements Action {
	
	private final String address;
	private final int port;
	private final boolean keyAuth;
	private final String user;
	private final String auth;
	private final List<String> commands;
	
	
	public Ssher(String address, int port, String user, boolean keyAuth, String auth,
			List<String> commands) {
		super();
		this.address = address;
		this.port = port;
		this.user = user;
		this.auth = auth;
		this.keyAuth = keyAuth;
		this.commands = commands;
	}


	
	@Override
	public Event exec() {
		try {
			SshResult sr = keyAuth? execSshKeyAuth(user, auth, address,	port, commands): execSshPswAuth(user, auth, address,
						port, commands);
			Ret lastCommand = sr.getCommandResults().get(
					sr.getCommandResults().size() - 1);
			if (sr.isSuccessful()) {
				return new Event(EventType.SUCCESS, lastCommand.getStdout());
			} else{
				return new Event(EventType.FAILURE, address + ": ssh command '"
						+ lastCommand.getCommand() + "' failed: "
						+ lastCommand.getStderr());
			}

		} catch (JSchException | IOException e) {
			return new Event(EventType.EXCEPTION, e.toString());
		}

	}
	
	
	
	//TODO -- not a convenient way!
	public static SshResult execSshPswAuth(String user, String password, String host,
			int port, List<String> commands) throws JSchException, IOException {
		
		JSch jsch = new JSch();
		Session session = null;
		List<Ret> srl = new ArrayList<>();
		try {
			session = jsch.getSession(user, host, port);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();			
			
			batchExec(commands, srl, session);

			session.disconnect();
		} finally {
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
		}

		return new SshResult(srl);
	}
	
	
	public static SshResult execSshKeyAuth(String user, String keyPath, String host,
			int port, List<String> commands) throws JSchException, IOException {	
		JSch jsch = new JSch();
		jsch.addIdentity(keyPath);
		Session session = null;
		List<Ret> srl = new ArrayList<>();
		try {
			session = jsch.getSession(user, host, port);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();

			batchExec(commands, srl, session);

			session.disconnect();
		} finally {
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
		}
		return new SshResult(srl);
	}
	
	private static void batchExec(List<String> commands, List<Ret> results, Session session) throws JSchException, IOException{
		for (String com : commands) {
			Ret r = _exec(session, com);
			results.add(r);
			if(r.getRet() != 0){
				break;
			}
		}
	}
	
	private static Ret _exec(Session session, String command) throws JSchException, IOException {
		Channel channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(command);
		channel.setInputStream(null);
		OutputStream err = new ByteArrayOutputStream();
		((ChannelExec)channel).setErrStream(err);
		InputStream in = channel.getInputStream();
		channel.connect();

		byte[] tmp = new byte[1024];
		StringBuilder sb = new StringBuilder();
		while (true) {
			while (in.available() > 0) {
				int i = in.read(tmp, 0, 1024);
				if (i < 0)
					break;
				sb.append(new String(tmp, 0, i));
			}
			if (channel.isClosed()) {
				if (in.available() > 0)
					continue;
				return new Ret(command, channel.getExitStatus(), sb.toString(), err.toString());
			}
			try {
				Thread.sleep(500);
			} catch (Exception ee) {
			}
		}
	}
	
	public static class Ret{
		final int ret;
		final String stdout;
		final String stderr;
		final String command;
		public Ret(String command, int ret, String stdout, String stderr) {
			super();
			this.command = command;
			this.ret = ret;
			this.stdout = stdout;
			this.stderr = stderr;
		}
		public String getCommand() {
			return command;
		}
		
		public int getRet() {
			return ret;
		}

		public String getStdout() {
			return stdout;
		}
		
		public String getStderr() {
			return stderr;
		}
		
		@Override
		public String toString(){
			return "command=" + command + ", ret=" + ret;
		}
	}
	
	public static class SshResult{
		private final List<Ret> commandResults;
		private final boolean isSuccessful;
		
		public SshResult(List<Ret> commandResults) {
			super();
			this.commandResults = commandResults;
			this.isSuccessful = commandResults.stream().noneMatch(r -> r.getRet() != 0);
		}
		public List<Ret> getCommandResults() {
			return commandResults;
		}
		public boolean isSuccessful() {
			return isSuccessful;
		}
		
	}
	
}
