import { useState } from "react";
import axios from "axios";

const API = "http://localhost:8080";

function App() {

    const [page, setPage] = useState("login");
    const [token, setToken] = useState(
        localStorage.getItem("token")
    );

    const [user, setUser] = useState(
        JSON.parse(localStorage.getItem("user") || "null")
    );

    const [loginData, setLoginData] = useState({
        email: "",
        password: "",
    });

    const [registerData, setRegisterData] = useState({
        name: "",
        email: "",
        password: "",
    });

    const [wallet, setWallet] = useState(null);
    const [transactions, setTransactions] = useState([]);

    const [amount, setAmount] = useState("");
    const [receiverWalletId, setReceiverWalletId] = useState("");

    const [message, setMessage] = useState("");

    const [adminStats, setAdminStats] = useState(null);
    const [adminUsers, setAdminUsers] = useState([]);
    const [adminTransactions, setAdminTransactions] = useState([]);

    /* =========================
       LOGIN
    ========================= */

    const login = async (e) => {

        e.preventDefault();

        try {

            const response = await axios.post(
                `${API}/api/auth/login`,
                loginData
            );

            const jwt = response.data.token;

            localStorage.setItem("token", jwt);
            setToken(jwt);

            const usersResponse = await axios.get(
                `${API}/api/users`,
                {
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                    },
                }
            );

            const currentUser =
                usersResponse.data.find(
                    (u) => u.email === loginData.email
                );

            localStorage.setItem(
                "user",
                JSON.stringify(currentUser)
            );

            setUser(currentUser);

            if (currentUser?.role === "ADMIN") {

                setPage("admin");

                await loadAdminData(jwt);

            } else {

                setPage("dashboard");

            }

            setMessage("Login successful");

        } catch (error) {

            setMessage(
                error.response?.data?.message ||
                "Invalid email or password"
            );
        }
    };


    /* =========================
       REGISTER
    ========================= */

    const register = async (e) => {

        e.preventDefault();

        try {

            await axios.post(
                `${API}/api/users`,
                registerData
            );

            setLoginData({
                email: registerData.email,
                password: registerData.password,
            });

            setRegisterData({
                name: "",
                email: "",
                password: "",
            });

            setMessage(
                "Registration successful. Please login."
            );

            setPage("login");

        } catch (error) {

            setMessage(
                error.response?.data?.message ||
                "Registration failed"
            );
        }
    };


    /* =========================
       WALLET
    ========================= */

    const createWallet = async () => {

        try {

            const response = await axios.post(
                `${API}/api/wallets/create/${user.id}`,
                {},
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            setWallet(response.data);

            setMessage(
                "Wallet created successfully"
            );

        } catch (error) {

            setMessage(
                error.response?.data?.message ||
                "Wallet creation failed"
            );
        }
    };


    const loadWallet = async () => {

        try {

            const response = await axios.get(
                `${API}/api/wallets/${user.id}`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            setWallet(response.data);

            loadTransactions(
                response.data.id
            );

        } catch (error) {

            setWallet(null);
        }
    };


    const loadTransactions = async (walletId) => {

        try {

            const response = await axios.get(
                `${API}/api/transactions/wallet/${walletId}?page=0&size=10`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            setTransactions(
                response.data.content || []
            );

        } catch (error) {

            setTransactions([]);
        }
    };


    /* =========================
       DEPOSIT
    ========================= */

    const deposit = async () => {

        try {

            await axios.post(
                `${API}/api/transactions/deposit/${wallet.id}?amount=${amount}`,
                {},
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            setAmount("");

            setMessage(
                "Deposit successful"
            );

            await loadWallet();

        } catch (error) {

            setMessage(
                error.response?.data?.message ||
                "Deposit failed"
            );
        }
    };


    /* =========================
       WITHDRAW
    ========================= */

    const withdraw = async () => {

        try {

            await axios.post(
                `${API}/api/transactions/withdraw/${wallet.id}?amount=${amount}`,
                {},
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            setAmount("");

            setMessage(
                "Withdrawal successful"
            );

            await loadWallet();

        } catch (error) {

            setMessage(
                error.response?.data?.message ||
                "Withdrawal failed"
            );
        }
    };


    /* =========================
       TRANSFER
    ========================= */

    const transfer = async () => {

        try {

            await axios.post(
                `${API}/api/transactions/transfer?fromWalletId=${wallet.id}&toWalletId=${receiverWalletId}&amount=${amount}`,
                {},
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            setAmount("");
            setReceiverWalletId("");

            setMessage(
                "Transfer successful"
            );

            await loadWallet();

        } catch (error) {

            setMessage(
                error.response?.data?.message ||
                "Transfer failed"
            );
        }
    };


    /* =========================
       ADMIN DATA
    ========================= */

    const loadAdminData = async (jwt = token) => {

        try {

            const config = {
                headers: {
                    Authorization: `Bearer ${jwt}`,
                },
            };

            const statsResponse =
                await axios.get(
                    `${API}/api/admin/stats`,
                    config
                );

            const usersResponse =
                await axios.get(
                    `${API}/api/admin/users`,
                    config
                );

            const transactionsResponse =
                await axios.get(
                    `${API}/api/admin/transactions`,
                    config
                );

            setAdminStats(
                statsResponse.data
            );

            setAdminUsers(
                usersResponse.data
            );

            setAdminTransactions(
                transactionsResponse.data
            );

        } catch (error) {

            setMessage(
                error.response?.data?.message ||
                "Unable to load admin data"
            );
        }
    };


    /* =========================
       LOGOUT
    ========================= */

    const logout = () => {

        localStorage.removeItem("token");
        localStorage.removeItem("user");

        setToken(null);
        setUser(null);

        setWallet(null);
        setTransactions([]);

        setAdminStats(null);
        setAdminUsers([]);
        setAdminTransactions([]);

        setPage("login");

        setMessage("");
    };


    /* =========================
       REGISTER PAGE
    ========================= */

    if (!token && page === "register") {

        return (

            <div className="app">

                <div className="card auth-card">

                    <h1>💳 PayShield</h1>

                    <p className="subtitle">
                        Create your account
                    </p>

                    {message && (
                        <div className="message">
                            {message}
                        </div>
                    )}

                    <form onSubmit={register}>

                        <input
                            type="text"
                            placeholder="Full Name"
                            value={registerData.name}
                            onChange={(e) =>
                                setRegisterData({
                                    ...registerData,
                                    name: e.target.value,
                                })
                            }
                            required
                        />

                        <input
                            type="email"
                            placeholder="Email"
                            value={registerData.email}
                            onChange={(e) =>
                                setRegisterData({
                                    ...registerData,
                                    email: e.target.value,
                                })
                            }
                            required
                        />

                        <input
                            type="password"
                            placeholder="Password"
                            value={registerData.password}
                            onChange={(e) =>
                                setRegisterData({
                                    ...registerData,
                                    password: e.target.value,
                                })
                            }
                            required
                        />

                        <button type="submit">
                            Create Account
                        </button>

                    </form>

                    <p>

                        Already have an account?{" "}

                        <span
                            className="link"
                            onClick={() => {
                                setPage("login");
                                setMessage("");
                            }}
                        >
              Login
            </span>

                    </p>

                </div>

            </div>
        );
    }


    /* =========================
       LOGIN PAGE
    ========================= */

    if (!token) {

        return (

            <div className="app">

                <div className="card auth-card">

                    <h1>💳 PayShield</h1>

                    <p className="subtitle">
                        Secure Digital Wallet
                    </p>

                    <h2>Login</h2>

                    {message && (
                        <div className="message">
                            {message}
                        </div>
                    )}

                    <form onSubmit={login}>

                        <input
                            type="email"
                            placeholder="Email"
                            value={loginData.email}
                            onChange={(e) =>
                                setLoginData({
                                    ...loginData,
                                    email: e.target.value,
                                })
                            }
                            required
                        />

                        <input
                            type="password"
                            placeholder="Password"
                            value={loginData.password}
                            onChange={(e) =>
                                setLoginData({
                                    ...loginData,
                                    password: e.target.value,
                                })
                            }
                            required
                        />

                        <button type="submit">
                            Login
                        </button>

                    </form>

                    <p>

                        Don't have an account?{" "}

                        <span
                            className="link"
                            onClick={() => {
                                setPage("register");
                                setMessage("");
                            }}
                        >
              Register
            </span>

                    </p>

                </div>

            </div>
        );
    }


    /* =========================
       ADMIN DASHBOARD
    ========================= */

    if (
        page === "admin" &&
        user?.role === "ADMIN"
    ) {

        return (

            <div className="dashboard">

                <nav className="navbar">

                    <h2>
                        💳 PayShield Admin
                    </h2>

                    <div>

            <span>
              {user?.name}
            </span>

                        <button
                            className="logout"
                            onClick={logout}
                        >
                            Logout
                        </button>

                    </div>

                </nav>


                <main className="container">

                    <div className="welcome">

                        <h1>
                            Admin Dashboard 👨‍💼
                        </h1>

                        <p>
                            Monitor PayShield platform activity.
                        </p>

                    </div>


                    {message && (
                        <div className="message">
                            {message}
                        </div>
                    )}


                    <div className="actions">

                        <div className="balance-card">

                            <p>Total Users</p>

                            <h1>
                                {adminStats?.totalUsers || 0}
                            </h1>

                        </div>


                        <div className="balance-card">

                            <p>Total Transactions</p>

                            <h1>
                                {adminStats?.totalTransactions || 0}
                            </h1>

                        </div>

                    </div>


                    <div className="card transactions">

                        <div className="transaction-header">

                            <h2>
                                👥 All Users
                            </h2>

                            <button
                                className="secondary"
                                onClick={() =>
                                    loadAdminData()
                                }
                            >
                                Refresh
                            </button>

                        </div>


                        <div className="table-wrapper">

                            <table>

                                <thead>

                                <tr>

                                    <th>ID</th>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th>Role</th>

                                </tr>

                                </thead>


                                <tbody>

                                {adminUsers.map(
                                    (adminUser) => (

                                        <tr
                                            key={adminUser.id}
                                        >

                                            <td>
                                                {adminUser.id}
                                            </td>

                                            <td>
                                                {adminUser.name}
                                            </td>

                                            <td>
                                                {adminUser.email}
                                            </td>

                                            <td>
                                                {adminUser.role}
                                            </td>

                                        </tr>

                                    )
                                )}

                                </tbody>

                            </table>

                        </div>

                    </div>


                    <div className="card transactions">

                        <h2>
                            📜 All Transactions
                        </h2>


                        <div className="table-wrapper">

                            <table>

                                <thead>

                                <tr>

                                    <th>ID</th>
                                    <th>Reference</th>
                                    <th>Type</th>
                                    <th>Amount</th>
                                    <th>Status</th>
                                    <th>Date</th>

                                </tr>

                                </thead>


                                <tbody>

                                {adminTransactions.map(
                                    (transaction) => (

                                        <tr
                                            key={transaction.id}
                                        >

                                            <td>
                                                {transaction.id}
                                            </td>

                                            <td>
                                                {transaction.referenceId}
                                            </td>

                                            <td>
                                                {transaction.type}
                                            </td>

                                            <td>
                                                ₹{transaction.amount}
                                            </td>

                                            <td>
                                                {transaction.status}
                                            </td>

                                            <td>
                                                {new Date(
                                                    transaction.createdAt
                                                ).toLocaleString()}
                                            </td>

                                        </tr>

                                    )
                                )}

                                </tbody>

                            </table>

                        </div>

                    </div>

                </main>

            </div>
        );
    }


    /* =========================
       USER DASHBOARD
    ========================= */

    return (

        <div className="dashboard">

            <nav className="navbar">

                <h2>
                    💳 PayShield
                </h2>

                <div>

          <span>
            {user?.name || "User"}
          </span>

                    <button
                        className="logout"
                        onClick={logout}
                    >
                        Logout
                    </button>

                </div>

            </nav>


            <main className="container">

                <div className="welcome">

                    <h1>
                        Welcome, {user?.name} 👋
                    </h1>

                    <p>
                        Manage your digital wallet securely.
                    </p>

                </div>


                {message && (
                    <div className="message">
                        {message}
                    </div>
                )}


                {!wallet ? (

                    <div className="card wallet-card">

                        <h2>
                            💰 Your Wallet
                        </h2>

                        <p>
                            You don't have a wallet yet.
                        </p>

                        <button
                            onClick={createWallet}
                        >
                            Create Wallet
                        </button>

                        <button
                            className="secondary"
                            onClick={loadWallet}
                        >
                            Check Existing Wallet
                        </button>

                    </div>

                ) : (

                    <>

                        <div className="balance-card">

                            <p>
                                Available Balance
                            </p>

                            <h1>
                                ₹{Number(wallet.balance).toFixed(2)}
                            </h1>

                            <span>
                Wallet ID: {wallet.id}
              </span>

                        </div>


                        <div className="actions">


                            <div className="card">

                                <h2>
                                    💰 Deposit
                                </h2>

                                <input
                                    type="number"
                                    placeholder="Amount"
                                    value={amount}
                                    onChange={(e) =>
                                        setAmount(e.target.value)
                                    }
                                />

                                <button
                                    onClick={deposit}
                                >
                                    Deposit
                                </button>

                            </div>


                            <div className="card">

                                <h2>
                                    💸 Withdraw
                                </h2>

                                <input
                                    type="number"
                                    placeholder="Amount"
                                    value={amount}
                                    onChange={(e) =>
                                        setAmount(e.target.value)
                                    }
                                />

                                <button
                                    onClick={withdraw}
                                >
                                    Withdraw
                                </button>

                            </div>


                            <div className="card">

                                <h2>
                                    🔄 Transfer
                                </h2>

                                <input
                                    type="number"
                                    placeholder="Receiver Wallet ID"
                                    value={receiverWalletId}
                                    onChange={(e) =>
                                        setReceiverWalletId(
                                            e.target.value
                                        )
                                    }
                                />

                                <input
                                    type="number"
                                    placeholder="Amount"
                                    value={amount}
                                    onChange={(e) =>
                                        setAmount(e.target.value)
                                    }
                                />

                                <button
                                    onClick={transfer}
                                >
                                    Transfer
                                </button>

                            </div>

                        </div>


                        <div className="card transactions">

                            <div className="transaction-header">

                                <h2>
                                    📜 Recent Transactions
                                </h2>

                                <button
                                    className="secondary"
                                    onClick={() =>
                                        loadTransactions(
                                            wallet.id
                                        )
                                    }
                                >
                                    Refresh
                                </button>

                            </div>


                            {transactions.length === 0 ? (

                                <p>
                                    No transactions found.
                                </p>

                            ) : (

                                <div className="table-wrapper">

                                    <table>

                                        <thead>

                                        <tr>

                                            <th>
                                                Reference
                                            </th>

                                            <th>
                                                Type
                                            </th>

                                            <th>
                                                Amount
                                            </th>

                                            <th>
                                                Status
                                            </th>

                                            <th>
                                                Date
                                            </th>

                                        </tr>

                                        </thead>


                                        <tbody>

                                        {transactions.map(
                                            (transaction) => (

                                                <tr
                                                    key={transaction.id}
                                                >

                                                    <td>
                                                        {transaction.referenceId}
                                                    </td>

                                                    <td>
                                                        {transaction.type}
                                                    </td>

                                                    <td>
                                                        ₹{transaction.amount}
                                                    </td>

                                                    <td>
                                                        {transaction.status}
                                                    </td>

                                                    <td>
                                                        {new Date(
                                                            transaction.createdAt
                                                        ).toLocaleString()}
                                                    </td>

                                                </tr>

                                            )
                                        )}

                                        </tbody>

                                    </table>

                                </div>

                            )}

                        </div>

                    </>

                )}

            </main>

        </div>
    );
}

export default App;